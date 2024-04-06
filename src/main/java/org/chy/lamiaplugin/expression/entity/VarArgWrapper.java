package org.chy.lamiaplugin.expression.entity;

import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.entity.MethodParameterWrapper;
import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.chy.lamia.convert.core.expression.parse.entity.ArgWrapper;

import java.util.function.Consumer;

public class VarArgWrapper extends PsiArgWrapper {

    String varName;
    TypeDefinition varType;

    boolean isMethodInvoke = false;


    public VarArgWrapper(Expression expression, String name) {
        super(expression, name);
        this.varName = name;
    }


    public boolean isMethodInvoke() {
        return isMethodInvoke;
    }

    public void setMethodInvoke(boolean methodInvoke) {
        isMethodInvoke = methodInvoke;
    }

    public TypeDefinition getVarType() {
        return varType;
    }

    public void setVarType(TypeDefinition varType) {
        this.varType = varType;
    }
}
