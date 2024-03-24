package org.chy.lamiaplugin.expression.entity;

import com.chy.lamia.convert.core.components.entity.Expression;

/**
 * 方法引用参数包装类, 如 User::getName 这样的参数
 */
public class ClassAccessArgWrapper extends PsiArgWrapper {

    /**
     * 引用的类名
     */
    String classPath;


    public ClassAccessArgWrapper(Expression expression, String classPath) {
        super(expression, classPath);
        this.classPath = classPath;
    }


    public String getClassPath() {
        return classPath;
    }

}
