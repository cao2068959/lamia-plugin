package org.chy.lamiaplugin.components;

import com.intellij.psi.JavaRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiVariable;

import java.util.HashSet;
import java.util.Set;

public class VariableCollector extends JavaRecursiveElementWalkingVisitor {
    private final Set<PsiVariable> variables = new HashSet<>();

    @Override
    public void visitReferenceExpression(PsiReferenceExpression expression) {
        super.visitReferenceExpression(expression);
        PsiElement resolved = expression.resolve();
        if (resolved instanceof PsiVariable) {
            variables.add((PsiVariable) resolved);
        }
    }

    public Set<PsiVariable> getVariables() {
        return variables;
    }
}