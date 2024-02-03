package org.chy.lamiaplugin.expression.entity;

import com.intellij.psi.PsiMethodCallExpression;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * lamia 关联关系的 class包装对象
 *
 * @author bignosecat
 */
public class RelationClassWrapper {

    String classPath;

    Set<String> filedNames;

    LamiaExpression lamiaExpression;

    public RelationClassWrapper(String classPath) {
        this.classPath = classPath;
    }

    public void addFiled(String name) {
        if (filedNames == null) {
            filedNames = new HashSet<>();
        }
        filedNames.add(name);
    }

    public boolean isContainFiled(String name) {
        if (filedNames == null) {
            return false;
        }
        return filedNames.contains(name);
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public void setFiledNames(Set<String> filedNames) {
        this.filedNames = filedNames;
    }


    public LamiaExpression getLamiaExpression() {
        return lamiaExpression;
    }

    public void setLamiaExpression(LamiaExpression lamiaExpression) {
        this.lamiaExpression = lamiaExpression;
    }

    public String getClassPath() {
        return classPath;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RelationClassWrapper that)) {
            return false;
        }
        return Objects.equals(getClassPath(), that.getClassPath()) && Objects.equals(getLamiaExpression(), that.getLamiaExpression());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClassPath(), getLamiaExpression());
    }
}
