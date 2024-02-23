package org.chy.lamiaplugin.expression.entity;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethodCallExpression;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    public String getBelongClassPath() {
        PsiFile belongPsiFile = getBelongPsiFile();
        if (belongPsiFile != null) {
            return belongPsiFile.getVirtualFile().getCanonicalPath();
        }
        return null;
    }

    public PsiFile getBelongPsiFile() {
        if (psiFile != null) {
            return psiFile;
        }
        return expression.getContainingFile();
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
