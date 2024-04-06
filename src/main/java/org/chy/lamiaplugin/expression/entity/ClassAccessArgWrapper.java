package org.chy.lamiaplugin.expression.entity;

import com.chy.lamia.convert.core.components.entity.Expression;

/**
 * 类访问的参数如： User.class 这样的参数
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
