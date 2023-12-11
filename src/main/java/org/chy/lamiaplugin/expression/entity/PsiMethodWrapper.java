package org.chy.lamiaplugin.expression.entity;

import com.chy.lamia.convert.core.expression.parse.entity.ArgWrapper;
import com.chy.lamia.convert.core.expression.parse.entity.MethodWrapper;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiVariable;
import com.siyeh.ig.psiutils.VariableAccessUtils;
import org.chy.lamiaplugin.expression.components.StringExpression;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PsiMethodWrapper extends MethodWrapper {

    private final PsiMethodCallExpression methodCallExpression;

    public PsiMethodWrapper(String name, PsiMethodCallExpression methodCallExpression) {
        super(name);
        this.methodCallExpression = methodCallExpression;
    }

    public void initArgs(Consumer<PsiArgWrapper> useFun) {
        Set<PsiVariable> methodCallParam = VariableAccessUtils.collectUsedVariables(methodCallExpression);
        List<ArgWrapper> arg = methodCallParam.stream().map(psiVariable -> {
            PsiArgWrapper psiArgWrapper = new PsiArgWrapper();
            psiArgWrapper.setName(psiVariable.getName());
            psiArgWrapper.setExpression(new StringExpression(psiVariable.getName()));
            psiArgWrapper.setPsiVariable(psiVariable);
            psiArgWrapper.setUseFun(useFun);
            return psiArgWrapper;
        }).collect(Collectors.toList());
        setArgs(arg);
    }
}
