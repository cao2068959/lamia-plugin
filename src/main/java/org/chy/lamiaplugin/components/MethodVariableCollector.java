package org.chy.lamiaplugin.components;

import com.intellij.psi.JavaRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiVariable;

import java.util.HashSet;
import java.util.Set;

public class MethodVariableCollector extends JavaRecursiveElementWalkingVisitor {
    private final Set<PsiVariable> variables = new HashSet<>();

    @Override
    public void visitVariable(PsiVariable variable) {
        super.visitVariable(variable);
        variables.add(variable);
    }

    public Set<PsiVariable> getVariables() {
        return variables;
    }
}
