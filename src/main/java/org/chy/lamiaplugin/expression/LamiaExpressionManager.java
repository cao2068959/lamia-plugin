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
import com.intellij.psi.PsiMethodCallExpression;
import org.chy.lamiaplugin.exception.LamiaConvertException;
import org.chy.lamiaplugin.expression.components.SimpleNameHandler;
import org.chy.lamiaplugin.expression.components.StringExpression;
import org.chy.lamiaplugin.expression.components.statement.StringStatement;
import org.chy.lamiaplugin.expression.components.StringTreeFactory;
import org.chy.lamiaplugin.expression.components.type_resolver.IdeaJavaTypeResolverFactory;
import org.chy.lamiaplugin.expression.entity.LamiaExpression;
import org.chy.lamiaplugin.expression.entity.RelationClassWrapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class LamiaExpressionManager {
    Project project;
    static Map<Project, LamiaExpressionManager> instances = new ConcurrentHashMap<>();
    LamiaExpressionResolver expressionResolver = new LamiaExpressionResolver();
    private static final Logger LOG = Logger.getInstance(LamiaExpressionManager.class);

    /**
     * 被lamia表达式生成了表达式的类 的关联关系，key：依赖到的类的全路径，value：lamia表达式对象以及这个表达式所在的类
     */
    private Map<String, Set<RelationClassWrapper>> relationsCache = new ConcurrentHashMap<>();

    /**
     * 反向的依赖关系 key：lamia的表达式本身， value：这个表达式 涉及到的类以及对应的字段
     */
    private Map<LamiaExpression, Set<RelationClassWrapper>> reverseRelationsCache = new ConcurrentHashMap<>();


    public static LamiaExpressionManager getInstance(Project project) {
        LamiaExpressionManager lamiaExpressionManager = instances.get(project);
        if (lamiaExpressionManager == null) {
            synchronized (LamiaExpressionManager.class) {
                lamiaExpressionManager = instances.get(project);
                if (lamiaExpressionManager == null) {
                    lamiaExpressionManager = new LamiaExpressionManager(project);
                    instances.put(project, lamiaExpressionManager);
                }
            }
        }
        return lamiaExpressionManager;
    }


    private LamiaExpressionManager(Project project) {
        this.project = project;
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

    public LamiaConvertInfo resolvingExpression(PsiMethodCallExpression methodCall, Consumer<Exception> exceptionConsumer) {
        return expressionResolver.resolving(methodCall, exceptionConsumer);
    }

    public void updateDependentRelations(PsiMethodCallExpression expression) {

        LamiaExpression lamiaExpression = new LamiaExpression(expression);
        // 先删除原有的依赖关系，如果存在的话
        deleteDependentRelations(lamiaExpression);

        // 如果表达式无效了，那也不需要去添加额外的关系了
        if (!expression.isValid()) {
            return;
        }
        lamiaExpression.setPsiFile(expression.getContainingFile());
        // 获取到 这个lamia表达式的所有 依赖关系，key:依赖到的类的全路径，value: 这个类下面所有使用到的字段
        Map<String, Set<String>> relations = this.getParticipateVar(expression);
        // 添加新的依赖关系
        relations.forEach((classPath, fieldNames) -> {
            RelationClassWrapper relationClassWrapper = new RelationClassWrapper(classPath);
            relationClassWrapper.setFiledNames(fieldNames);
            relationClassWrapper.setLamiaExpression(lamiaExpression);
            this.addRelations(relationClassWrapper);
        });
    }

    public void deleteDependentRelations(PsiMethodCallExpression expression) {
        LamiaExpression lamiaExpression = new LamiaExpression(expression);
        deleteDependentRelations(lamiaExpression);
    }

    private void deleteDependentRelations(LamiaExpression lamiaExpression) {
        Set<RelationClassWrapper> relationClassWrappers = reverseRelationsCache.remove(lamiaExpression);
        // 没有任何的依赖
        if (relationClassWrappers == null) {
            return;
        }
        relationClassWrappers.forEach(relationClassWrapper -> {
            String classPath = relationClassWrapper.getClassPath();
            // 获取到这个 类依赖了哪些 lamia表达式
            Set<RelationClassWrapper> relationLamia = relationsCache.get(classPath);
            if (relationLamia != null) {
                relationLamia.remove(relationClassWrapper);
                if (relationLamia.isEmpty()) {
                    relationsCache.remove(classPath);
                }
            }
        });
    }

    /**
     * 获取指定的类 关联了那几个 lamiaExpression
     * @param classPath
     * @return
     */
    public Set<RelationClassWrapper> getRelationLamia(String classPath){
        Set<RelationClassWrapper> relationClassWrappers = relationsCache.get(classPath);
        if (relationClassWrappers == null) {
            return new HashSet<>();
        }
        return relationClassWrappers;
    }

}
