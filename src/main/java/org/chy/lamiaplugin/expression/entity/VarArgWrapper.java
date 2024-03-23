package org.chy.lamiaplugin.expression.entity;

import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.expression.parse.entity.ArgWrapper;

import java.util.function.Consumer;

public class VarArgWrapper extends PsiArgWrapper {

    String varName;
    String varType;


    public VarArgWrapper(Expression expression, String name) {
        super(expression, name);
        this.varName = name;
    }

    public String getVarType() {
        return varType;
    }

    public void setVarType(String varType) {
        this.varType = varType;
    }
}
