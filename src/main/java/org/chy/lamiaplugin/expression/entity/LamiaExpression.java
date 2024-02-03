package org.chy.lamiaplugin.expression.entity;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;

import java.util.Objects;

public class LamiaExpression {

    PsiMethodCallExpression expression;

    PsiFile psiFile;

    public LamiaExpression(PsiMethodCallExpression expression) {
        this.expression = expression;
    }

    public LamiaExpression(PsiMethodCallExpression expression, PsiFile psiFile) {
        this.expression = expression;
        this.psiFile = psiFile;
    }

    public void setPsiFile(PsiFile psiFile) {
        this.psiFile = psiFile;
    }

    public PsiMethodCallExpression getExpression() {
        return expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LamiaExpression that)) {
            return false;
        }
        return Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression);
    }
}
