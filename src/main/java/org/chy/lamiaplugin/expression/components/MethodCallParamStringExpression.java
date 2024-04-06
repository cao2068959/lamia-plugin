package org.chy.lamiaplugin.expression.components;

import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.entity.MethodParameterWrapper;
import com.chy.lamia.convert.core.entity.TypeDefinition;

public class MethodCallParamStringExpression extends StringExpression {

    private final TypeDefinition type;

    public MethodCallParamStringExpression(TypeDefinition type, String expression) {
        super(expression);
        this.type = type;
    }

    @Override
    public MethodParameterWrapper toMethodParameterWrapper() {
        MethodParameterWrapper parameterWrapper = new MethodParameterWrapper(type, this);
        parameterWrapper.setText(data);
        return parameterWrapper;
    }
}
