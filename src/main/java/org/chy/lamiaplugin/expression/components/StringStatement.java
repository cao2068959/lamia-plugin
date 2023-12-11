package org.chy.lamiaplugin.expression.components;

import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.Statement;

public class StringStatement implements Statement {

    String data;

    public StringStatement(String data) {
        this.data = data;
    }


    public StringStatement() {
    }

    @Override
    public Object get() {
        return data;
    }

    @Override
    public Expression getExpression() {
        return new StringExpression(data);
    }
}
