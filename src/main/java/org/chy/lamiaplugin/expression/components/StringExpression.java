package org.chy.lamiaplugin.expression.components;

import com.chy.lamia.convert.core.components.entity.Expression;

public class StringExpression implements Expression {


    String data;

    public StringExpression(String data) {
        this.data = data;
    }

    public StringExpression() {
    }

    @Override
    public Object get() {
        if (data == null) {
            return "null";
        }
        return data;
    }
}
