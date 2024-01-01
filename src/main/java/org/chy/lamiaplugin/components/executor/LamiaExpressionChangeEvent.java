package org.chy.lamiaplugin.components.executor;

import com.intellij.psi.PsiMethodCallExpression;
import org.chy.lamiaplugin.expression.entity.LamiaExpression;

public class LamiaExpressionChangeEvent extends Event {

    PsiMethodCallExpression LamiaExpression;


    public LamiaExpressionChangeEvent(PsiMethodCallExpression lamiaExpression) {
        super("LamiaExpressionChangeExecutor");
        this.LamiaExpression = lamiaExpression;
    }


    public PsiMethodCallExpression getLamiaExpression() {
        return LamiaExpression;
    }
}
