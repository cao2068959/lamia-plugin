package org.chy.lamiaplugin.expression.components;

import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.entity.MethodParameterWrapper;
import com.chy.lamia.convert.core.utils.struct.Pair;
import org.chy.lamiaplugin.exception.NoSuchMethodException;

import java.nio.file.NoSuchFileException;
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

    @Override
    public Pair<String, String> parseMethodReferenceOperator() {
        throw new NoSuchMethodException("StringExpression 不支持方法引用操作符");
    }

    @Override
    public MethodParameterWrapper toMethodParameterWrapper() {
        throw new NoSuchMethodException("StringExpression 不支持 toMethodParameterWrapper");
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

    @Override
    public String toString() {
        return (String) get();
    }
}
