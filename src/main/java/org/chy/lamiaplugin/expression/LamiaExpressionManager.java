package org.chy.lamiaplugin.expression;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.chy.lamia.convert.core.ConvertFactory;
import com.chy.lamia.convert.core.components.ComponentFactory;
import com.chy.lamia.convert.core.components.NameHandler;
import com.chy.lamia.convert.core.components.TreeFactory;
import com.chy.lamia.convert.core.components.TypeResolverFactory;
import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.Statement;
import com.chy.lamia.convert.core.entity.LamiaConvertInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import org.chy.lamiaplugin.components.executor.ScheduledBatchExecutor;
import org.chy.lamiaplugin.exception.LamiaConvertException;
import org.chy.lamiaplugin.expression.components.SimpleNameHandler;
import org.chy.lamiaplugin.expression.components.StringExpression;
import org.chy.lamiaplugin.expression.components.statement.StringStatement;
import org.chy.lamiaplugin.expression.components.StringTreeFactory;
import org.chy.lamiaplugin.expression.components.type_resolver.IdeaJavaTypeResolverFactory;
import org.chy.lamiaplugin.expression.entity.DependentWrapper;
import org.chy.lamiaplugin.expression.entity.LamiaExpression;
import org.chy.lamiaplugin.expression.entity.RelationClassWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LamiaExpressionManager {

    Project project;

    static Map<Project, LamiaExpressionManager> instances = new HashMap<>();

    /**
     * 被lamia表达式生成了表达式的类 的关联关系，key：依赖到的类的全路径，value：具体使用到的字段以及对应的表达式本身
     */
    private Map<String, Set<RelationClassWrapper>> relationsCache = new ConcurrentHashMap<>();

    /**
     * 反向的依赖关系 key：lamia的表达式本身， value：这个表达式 涉及到的类以及对应的字段
     */
    private Map<LamiaExpression, Set<RelationClassWrapper>> reverseRelationsCache = new ConcurrentHashMap<>();


    LamiaExpressionResolver expressionResolver = new LamiaExpressionResolver();

    private static final Logger LOG = Logger.getInstance(LamiaExpressionManager.class);

    public static LamiaExpressionManager getInstance(Project project) {

        return instances.get(project);
    }


    public LamiaExpressionManager(Project project) {
        this.project = project;
        instances.put(project, this);
        registerLamiaComponents();
    }


    private void registerLamiaComponents() {
        ComponentFactory.registerComponents(TreeFactory.class, new StringTreeFactory());
        ComponentFactory.registerComponents(TypeResolverFactory.class, new IdeaJavaTypeResolverFactory(project));
        ComponentFactory.registerEntityStructure(Expression.class, StringExpression::new);
        ComponentFactory.registerEntityStructure(Statement.class, StringStatement::new);
        ComponentFactory.registerComponents(NameHandler.class, new SimpleNameHandler());
    }


    public ConvertResult convert(PsiMethodCallExpression psiElement) {

        try {
            LamiaConvertInfo lamiaConvertInfo = expressionResolver.resolving(psiElement, e -> {
                LOG.warn("解析表达式失败", e);
                throw new LamiaConvertException("Parsing expression failed!!");
            });
            List<Statement> makeResult = ConvertFactory.INSTANCE.make(lamiaConvertInfo);
            return ConvertResult.success(convertString(makeResult));

        } catch (Exception e) {
            String failMsg;
            if (e instanceof LamiaConvertException convertException) {
                failMsg = convertException.getMessage();
            } else {
                LOG.warn("表达式生成异常", e);
                failMsg = "Generate conversion code exception";
            }
            return ConvertResult.fail(failMsg);
        }

    }

    public Map<String, Set<String>> getParticipateVar(PsiMethodCallExpression psiElement) {

        try {
            LamiaConvertInfo lamiaConvertInfo = expressionResolver.resolving(psiElement);
            return ConvertFactory.INSTANCE.getParticipateVar(lamiaConvertInfo);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }


    private String convertString(List<Statement> statements) {
        StringBuilder result = new StringBuilder();
        for (Statement statement : statements) {
            result.append(toString(statement)).append("\n");
        }
        return result.toString();
    }

    private String toString(Statement statement) {
        if (statement instanceof StringStatement stringStatement) {
            return stringStatement.getStatement(0);
        }
        return statement.get() + ";";
    }


    public void addRelations(RelationClassWrapper relationClassWrapper) {
        LamiaExpression lamiaExpression = relationClassWrapper.getLamiaExpression();
        if (lamiaExpression == null) {
            throw new RuntimeException("无效的参数 [relationClassWrapper: " + relationClassWrapper.getClassPath() + "] 中 lamiaExpression 不能为null");
        }
        String classPath = relationClassWrapper.getClassPath();

        relationsCache.computeIfAbsent(classPath, __ -> new ConcurrentHashSet<>()).add(relationClassWrapper);
        reverseRelationsCache.computeIfAbsent(lamiaExpression, __ -> new ConcurrentHashSet<>()).add(relationClassWrapper);
    }

}
