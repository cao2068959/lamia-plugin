package org.chy.lamiaplugin.expression.components;

import com.chy.lamia.convert.core.components.TreeFactory;
import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.Statement;
import org.chy.lamiaplugin.expression.components.statement.IfStatement;
import org.chy.lamiaplugin.expression.components.statement.StringStatement;

import java.util.*;

public class StringTreeFactory implements TreeFactory {

    @Override
    public Expression newClass(String classPath, List<Expression> newInstanceParam) {
        String param = param(newInstanceParam);
        ClassPathResult classPathResult = classPathHandle(classPath);
        StringExpression result = new StringExpression("new " + classPathResult.simpleClassPath + "(" + param + ")");
        classPathResult.allClassPath.forEach(result::addImportClassPath);
        return result;
    }

    @Override
    public Statement createVar(String instantName, String classPath, Expression newClass) {
        String value = toString(newClass);
        ClassPathResult classPathResult = classPathHandle(classPath);
        StringStatement stringStatement = new StringStatement(classPathResult.simpleClassPath + " " + instantName + " = " + value);
        classPathResult.allClassPath.forEach(stringStatement::addImportClassPath);
        return stringStatement;
    }

    @Override
    public Statement varAssign(String instantName, Expression newClass) {
        String value = toString(newClass);
        StringStatement stringStatement = new StringStatement(instantName + " = " + value);
        stringStatement.addImportClassPath(newClass);
        return stringStatement;
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
        ClassPathResult classPathResult = classPathHandle(classPath);
        StringExpression stringExpression = new StringExpression("(" + classPathResult.simpleClassPath + ")" + instant);
        classPathResult.allClassPath.forEach(stringExpression::addImportClassPath);
        return stringExpression;
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
     * 同时返回所有的全路径，包括参与泛型的
     * 如: java.util.Map<java.lang.String, java.lang.Object> map 将会返回 Map<String,Object> map, 同时返回 ["java.lang.String", "java.util.Map" , "java.lang.Object"]
     *
     * @return
     */
    private ClassPathResult classPathHandle(String classPath) {

        char[] charArray = classPath.toCharArray();

        // 3个索引从左到右分别代表 start 、 end、 lastDot
        int[] index = new int[]{-1, -1, -1};
        Set<String> allPath = new HashSet<>();
        Map<String, String> allName = new HashMap<>();
        StringBuilder resultBuilder = new StringBuilder();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (c == ' ') {
                // 其中一段结束
                classPathItemHandle(charArray, index, resultBuilder, allPath, allName);
                continue;
            }

            if (c == '<' || c == '>' || c == ',') {
                // 前面的结束，并且这个符号要一起跟后面
                classPathItemHandle(charArray, index, resultBuilder, allPath, allName);
                resultBuilder.append(c);
                if (c == ','){
                    resultBuilder.append(' ');
                }
                continue;
            }
            // 其他正常的字符去移动下标
            if (c == '.') {
                index[2] = i;
                continue;
            }
            int start = index[0];
            index[0] = start == -1 ? i : start;
            index[1] = i;
        }

        classPathItemHandle(charArray, index, resultBuilder, allPath, allName);

        return new ClassPathResult(resultBuilder.toString(), allPath);

    }

    private void classPathItemHandle(char[] charArray, int[] index, StringBuilder resultBuilder,
                                     Set<String> allPath, Map<String, String> allName) {
        int start = index[0];
        int end = index[1];

        if (start == -1 || end == -1) {
            return;
        }
        String classPath = new String(charArray, start, end - start + 1);
        allPath.add(classPath);

        int dot = index[2];
        String name;
        if (dot == -1) {
            name = classPath;
        } else {
            name = new String(charArray, dot + 1, end - dot);
        }

        String existAlisClassPath = allName.get(name);
        // 这个别名不存在可以使用
        if (existAlisClassPath == null) {
            allName.put(name, classPath);

        } else {
            // 已经存在了看是否是相同类型
            name = existAlisClassPath.equals(classPath) ? name : classPath;
        }
        resultBuilder.append(name);

        // 使用完了，全部置空
        index[0] = -1;
        index[1] = -1;
        index[2] = -1;
    }



    static class ClassPathResult {
        public static ClassPathResult empty = new ClassPathResult("", new HashSet<>());

        String simpleClassPath;
        Set<String> allClassPath;

        public ClassPathResult(String simpleClassPath, Set<String> allClassPath) {
            this.simpleClassPath = simpleClassPath;
            this.allClassPath = allClassPath;
        }

        public ClassPathResult() {
        }
    }

}
