package org.chy.lamiaplugin.expression.components;

import com.chy.lamia.convert.core.entity.MethodParameterWrapper;

public class ParamStringExpression extends StringExpression {

    String paramName;

    public ParamStringExpression(String name) {
        super(name);
        this.paramName = name;
    }

    @Override
    public MethodParameterWrapper toMethodParameterWrapper() {
        return new MethodParameterWrapper(paramName);


    }
}
