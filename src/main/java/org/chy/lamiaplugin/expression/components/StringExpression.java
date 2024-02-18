package org.chy.lamiaplugin.expression.components;

import com.chy.lamia.convert.core.components.entity.Expression;

import java.util.HashSet;
import java.util.Set;

public class StringExpression implements Expression {


    String data;
    private Set<String> importClassPath;

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

    public Set<String> getImportClassPath() {
        return importClassPath;
    }

    public void addImportClassPath(String data) {
        if (importClassPath == null) {
            importClassPath = new HashSet<>();
        }
        importClassPath.add(data);
    }
}
