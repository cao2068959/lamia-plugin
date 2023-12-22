package org.chy.lamiaplugin.expression.components.statement;

import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.Statement;
import org.chy.lamiaplugin.expression.components.StringExpression;

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

    public String getStatement(int level) {
        StringBuilder result = new StringBuilder();
        return result.append(blank(level)).append(data).append(";").toString();
    }

    protected String blank(int level) {
        int count = (level + 1) * 4;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            result.append(" ");
        }
        return result.toString();
    }


    @Override
    public Expression getExpression() {
        return new StringExpression(data);
    }
}
