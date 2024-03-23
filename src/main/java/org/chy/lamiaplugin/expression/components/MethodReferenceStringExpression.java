package org.chy.lamiaplugin.expression.components;

import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.utils.struct.Pair;

import java.util.HashSet;
import java.util.Set;

public class MethodReferenceStringExpression extends StringExpression {

    Pair<String, String> data;

    public MethodReferenceStringExpression(Pair<String, String> data) {
        this.data = data;
    }

    @Override
    public Object get() {
        if (data == null) {
            return "null";
        }
        return data;
    }

    @Override
    public Pair<String, String> parseMethodReferenceOperator() {
        return data;
    }

}
