package org.chy.lamiaplugin.expression.entity;

import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.expression.parse.entity.ArgWrapper;
import com.intellij.psi.PsiVariable;

import java.util.function.Consumer;

public class PsiArgWrapper extends ArgWrapper {

    Consumer<PsiArgWrapper> useFun;

    public PsiArgWrapper(Expression expression, String name) {
        super(expression, name);
    }

    @Override
    public void use() {
        useFun.accept(this);
    }

    public Consumer<PsiArgWrapper> getUseFun() {
        return useFun;
    }

    public void setUseFun(Consumer<PsiArgWrapper> useFun) {
        this.useFun = useFun;
    }
}
