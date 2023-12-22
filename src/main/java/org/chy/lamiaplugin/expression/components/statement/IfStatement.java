package org.chy.lamiaplugin.expression.components.statement;

import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.Statement;

import java.util.List;

public class IfStatement extends StringStatement {

    Expression determine;
    List<Statement> trueBlock;
    List<Statement> falseBlock;

    public IfStatement(Expression determine) {
        this.determine = determine;
    }

    @Override
    public Object get() {
        return getStatement(0);
    }

    @Override
    public String getStatement(int level) {
        StringBuilder result = new StringBuilder();
        result.append(blank(level)).append("if (").append(determine.get()).append(") ");
        appendBlock(result, level, trueBlock);
        if (falseBlock != null && !falseBlock.isEmpty()) {
            result.append(" else ");
            appendBlock(result, level, falseBlock);
        }
        return result.toString();
    }

    protected void appendBlock(StringBuilder result, int level, List<Statement> data) {
        result.append("{\n");
        int newLevel = level + 1;
        for (Statement item : data) {
            result.append(toString(newLevel, item)).append("\n");
        }
        result.append(blank(level)).append("}");
    }

    private String toString(int level, Statement statement) {
        if (statement instanceof StringStatement stringStatement) {
            return stringStatement.getStatement(level);
        }
        return statement.get() + ";";
    }

    protected void appendLine(StringBuilder result, int level, String data) {
        result.append(blank(level)).append(data).append(";\n");
    }

    public void setTrueBlock(List<Statement> trueBlock) {
        this.trueBlock = trueBlock;
    }

    public void setFalseBlock(List<Statement> falseBlock) {
        this.falseBlock = falseBlock;
    }
}
