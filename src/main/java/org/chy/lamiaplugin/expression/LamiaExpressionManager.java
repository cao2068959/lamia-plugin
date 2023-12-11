package org.chy.lamiaplugin.expression;

import com.chy.lamia.convert.core.components.ComponentFactory;
import com.chy.lamia.convert.core.components.TreeFactory;
import com.chy.lamia.convert.core.components.TypeResolverFactory;
import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.Statement;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.chy.lamiaplugin.expression.components.StringExpression;
import org.chy.lamiaplugin.expression.components.StringStatement;
import org.chy.lamiaplugin.expression.components.StringTreeFactory;
import org.chy.lamiaplugin.expression.components.type_resolver.IdeaJavaTypeResolverFactory;

import java.util.HashMap;
import java.util.Map;

public class LamiaExpressionManager {

    Project project;

    static Map<Project, LamiaExpressionManager> instances = new HashMap<>();

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
    }


    public void convert(PsiElement psiElement) {
        expressionResolver.resolving(psiElement);

    }
}
