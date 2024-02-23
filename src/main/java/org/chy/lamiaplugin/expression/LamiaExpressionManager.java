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
import org.apache.commons.lang3.StringUtils;
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

    ConvertFactory convertFactory;

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
        convertFactory = new ConvertFactory();
        registerLamiaComponents();
    }


    private void registerLamiaComponents() {
        ComponentFactory.registerInstanceComponents(convertFactory, TypeResolverFactory.class, new IdeaJavaTypeResolverFactory(project));
    }


    public ConvertResult convert(PsiMethodCallExpression psiElement) {

        try {
            LamiaConvertInfo lamiaConvertInfo = expressionResolver.resolving(psiElement, e -> {
                LOG.warn("解析表达式失败", e);
                throw new LamiaConvertException("Parsing expression failed!!");
            });
            List<Statement> makeResult = convertFactory.make(lamiaConvertInfo);
            return convert(makeResult);

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
            return convertFactory.getParticipateVar(lamiaConvertInfo);
        } catch (Exception e) {
            return null;
        }
    }


    private ConvertResult convert(List<Statement> statements) {
        StringBuilder code = new StringBuilder();
        Set<String> allImportClass = new HashSet<>();
        for (Statement statement : statements) {
            code.append(convertStatement(statement, allImportClass)).append("\n");
        }
        ConvertResult result = ConvertResult.success(code.toString());
        result.setImportClassPath(allImportClass);
        return result;
    }

    private String convertStatement(Statement statement, Set<String> allImportClass) {
        if (statement instanceof StringStatement stringStatement) {
            Set<String> importClassPath = stringStatement.getImportClassPath();
            if (importClassPath != null) {
                allImportClass.addAll(importClassPath);
            }
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
        // 如果表达式无效了，那也不需要去添加额外的关系了
        if (!expression.isValid()) {
            // 无效的表达式了，直接删除原有的依赖
            deleteDependentRelations(lamiaExpression);
            return;
        }
        lamiaExpression.setPsiFile(expression.getContainingFile());
        // 获取到 这个lamia表达式的所有 依赖关系，key:依赖到的类的全路径，value: 这个类下面所有使用到的字段
        Map<String, Set<String>> relations = this.getParticipateVar(expression);

        if (relations != null) {
            // 在添加之前去删除原有的连接关系
            deleteDependentRelations(lamiaExpression);
            // 添加新的依赖关系
            relations.forEach((classPath, fieldNames) -> {
                RelationClassWrapper relationClassWrapper = new RelationClassWrapper(classPath);
                relationClassWrapper.setFiledNames(fieldNames);
                relationClassWrapper.setLamiaExpression(lamiaExpression);
                this.addRelations(relationClassWrapper);
            });
        }


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
     *
     * @param classPath
     * @return
     */
    public Set<RelationClassWrapper> getRelationLamia(String classPath) {
        Set<RelationClassWrapper> relationClassWrappers = relationsCache.get(classPath);
        if (relationClassWrappers == null) {
            return new HashSet<>();
        }
        return relationClassWrappers;
    }

    public Set<RelationClassWrapper> getRelation(LamiaExpression lamiaExpression) {
        return reverseRelationsCache.get(lamiaExpression);
    }


}
