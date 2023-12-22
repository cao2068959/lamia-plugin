package org.chy.lamiaplugin.expression.components;

import com.chy.lamia.convert.core.components.TreeFactory;
import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.Statement;
import org.chy.lamiaplugin.expression.components.statement.IfStatement;
import org.chy.lamiaplugin.expression.components.statement.StringStatement;

import java.util.List;

public class StringTreeFactory implements TreeFactory {

    @Override
    public Expression newClass(String classPath, List<Expression> newInstanceParam) {
        String param = param(newInstanceParam);
        return new StringExpression("new " + classPath + "(" + param + ")");
    }

    @Override
    public Statement createVar(String instantName, String classPath, Expression newClass) {
        String value = toString(newClass);
        return new StringStatement(classPath + " " + instantName + " = " + value);
    }

    @Override
    public Statement varAssign(String instantName, Expression newClass) {
        String value = toString(newClass);
        return new StringStatement(instantName + " = " + value);
    }

    @Override
    public Expression toExpression(String newInstant) {
        return new StringExpression(newInstant);
    }

    @Override
    public Expression geStringExpression(String supplyName) {
        return new StringExpression("\"" + supplyName + "\"");
    }

    @Override
    public Statement execMethod(String instant, String method, List<Expression> args) {
        String param = param(args);
        return new StringStatement(instant + "." + method + "(" + param + ")");
    }

    @Override
    public Statement execMethod(Expression expression, String method, List<Expression> args) {
        String param = param(args);
        String instant = toString(expression);
        return new StringStatement(instant + "." + method + "(" + param + ")");
    }

    @Override
    public Statement createReturn(String newInstantName) {
        return new StringStatement("return " + newInstantName);
    }

    @Override
    public Expression typeCast(String classPath, Expression expression) {
        String instant = toString(expression);

        return new StringExpression("(" + classPath + ")" + instant);
    }

    @Override
    public Statement createIf(Expression judge, List<Statement> trueStatements, List<Statement> falseStatements) {
        IfStatement result = new IfStatement(judge);
        result.setTrueBlock(trueStatements);
        result.setFalseBlock(falseStatements);
        return result;
    }

    @Override
    public Expression createVarNotEqNull(Expression varExpression) {
        String value = toString(varExpression);
        return new StringExpression(value + " != null");
    }


    private String param(List<Expression> args) {
        if (args == null || args.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < args.size(); i++) {
            Expression expression = args.get(i);
            String value = toString(expression);
            if (i != 0) {
                result.append(", ");
            }
            result.append(value);
        }
        return result.toString();
    }


    private String toString(Expression expression) {
        if (expression == null) {
            return "null";
        }
        Object result = expression.get();
        if (result == null) {
            return "null";
        }
        return result.toString();
    }

    private String toString(List<Statement> expressions, int blankCount) {
        if (expressions.isEmpty()) {
            return "\r\n";
        }
        String blank = getBlank(blankCount);
        StringBuilder result = new StringBuilder();
        for (Statement expression : expressions) {
            result.append(blank).append(expression.get()).append("\n");
        }
        return result.toString();
    }

    private String getBlank(int count) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            result.append(" ");
        }
        return result.toString();
    }

    /**
     * 对 classpath 进行简写 比如 com.chy.User 只显示 User
     *
     * @return
     */
    private String classAbb(String classPath) {

    }

}
