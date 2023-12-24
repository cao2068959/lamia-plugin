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
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.spi.psi.SPIFile;
import org.chy.lamiaplugin.expression.components.SimpleNameHandler;
import org.chy.lamiaplugin.expression.components.StringExpression;
import org.chy.lamiaplugin.expression.components.statement.StringStatement;
import org.chy.lamiaplugin.expression.components.StringTreeFactory;
import org.chy.lamiaplugin.expression.components.type_resolver.IdeaJavaTypeResolverFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LamiaExpressionManager {

    Project project;

    static Map<Project, LamiaExpressionManager> instances = new HashMap<>();

    private Map<String, Set<PsiFile>> dependentCache = new ConcurrentHashMap<>();

    LamiaExpressionResolver expressionResolver = new LamiaExpressionResolver();

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


    public String convert(PsiElement psiElement) {
        LamiaConvertInfo lamiaConvertInfo = expressionResolver.resolving(psiElement);
        List<Statement> makeResult = ConvertFactory.INSTANCE.make(lamiaConvertInfo);
        return convertString(makeResult);
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

    public void addDependent(String classpath, PsiFile psiFile) {
        if (classpath == null) {
            return;
        }
        Set<PsiFile> psiFiles = dependentCache.computeIfAbsent(classpath, k -> new ConcurrentHashSet<>());
        psiFiles.add(psiFile);
    }
}
