package org.chy.lamiaplugin.expression.entity;

import com.chy.lamia.convert.core.expression.parse.entity.ArgWrapper;
import com.chy.lamia.convert.core.components.entity.Expression;

/**
 * 方法引用参数包装类, 如 User::getName 这样的参数
 */
public class MethodReferenceArgWrapper extends PsiArgWrapper {

    /**
     *  引用的类名
     */
    String refClassName;

    /**
     * 引用的方法名
     */
    String methodName;

    public MethodReferenceArgWrapper(Expression expression, String name) {
        super(expression, name);
    }


    public String getRefClassName() {
        return refClassName;
    }

    public void setRefClassName(String refClassName) {
        this.refClassName = refClassName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
