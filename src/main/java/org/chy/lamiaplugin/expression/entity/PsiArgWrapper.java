package org.chy.lamiaplugin.expression.entity;

import com.chy.lamia.convert.core.expression.parse.entity.ArgWrapper;
import com.intellij.psi.PsiVariable;

import java.util.function.Consumer;

public class PsiArgWrapper extends ArgWrapper {

    PsiVariable psiVariable;

    Consumer<PsiArgWrapper> useFun;

    @Override
    public void use() {
        useFun.accept(this);
    }

    public PsiVariable getPsiVariable() {
        return psiVariable;
    }

    public void setPsiVariable(PsiVariable psiVariable) {
        this.psiVariable = psiVariable;
    }

    public Consumer<PsiArgWrapper> getUseFun() {
        return useFun;
    }

    public void setUseFun(Consumer<PsiArgWrapper> useFun) {
        this.useFun = useFun;
    }
}
