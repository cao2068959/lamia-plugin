package org.chy.lamiaplugin.components.executor;

import com.intellij.psi.PsiMethodCallExpression;
import org.chy.lamiaplugin.expression.entity.LamiaExpression;

public class LamiaExpressionChangeEvent extends Event {

    PsiMethodCallExpression LamiaExpression;

    ChangeType type;


    public LamiaExpressionChangeEvent(PsiMethodCallExpression lamiaExpression, ChangeType type) {
        super("LamiaExpressionChangeExecutor");
        this.LamiaExpression = lamiaExpression;
        this.type = type;
    }


    public PsiMethodCallExpression getLamiaExpression() {
        return LamiaExpression;
    }
}
