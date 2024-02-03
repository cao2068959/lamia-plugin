package org.chy.lamiaplugin.components.executor;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethodCallExpression;
import org.chy.lamiaplugin.expression.entity.LamiaExpression;

public class LamiaExpressionChangeEvent extends Event {

    PsiMethodCallExpression LamiaExpression;

    ChangeType type;


    public LamiaExpressionChangeEvent(PsiMethodCallExpression lamiaExpression, ChangeType type, Project project) {
        super("LamiaExpressionChangeExecutor-" + project.getName());
        this.LamiaExpression = lamiaExpression;
        this.type = type;
    }


    public PsiMethodCallExpression getLamiaExpression() {
        return LamiaExpression;
    }
}
