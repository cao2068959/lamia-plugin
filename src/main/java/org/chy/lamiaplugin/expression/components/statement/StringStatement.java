package org.chy.lamiaplugin.expression.components.statement;

import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.Statement;
import org.chy.lamiaplugin.expression.components.StringExpression;

import java.util.HashSet;
import java.util.Set;

public class StringStatement implements Statement {

    String data;

    Set<String> importClassPath;

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


    public Set<String> getImportClassPath() {
        return importClassPath;
    }

    public void addImportClassPath(String classPath) {
        if (importClassPath == null) {
            importClassPath = new HashSet<>();
        }
        importClassPath.add(classPath);
    }

    public void addImportClassPath(Expression expression) {
        if (expression instanceof StringExpression stringExpression) {
            importClassPath.addAll(stringExpression.getImportClassPath());
        }
    }
}
