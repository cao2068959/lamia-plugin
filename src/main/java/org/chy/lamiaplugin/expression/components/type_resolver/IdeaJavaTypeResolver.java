package org.chy.lamiaplugin.expression.components.type_resolver;

import com.chy.lamia.convert.core.components.TypeResolver;
import com.chy.lamia.convert.core.components.TypeResolverFactory;
import com.chy.lamia.convert.core.entity.Constructor;
import com.chy.lamia.convert.core.entity.Getter;
import com.chy.lamia.convert.core.entity.Setter;
import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdeaJavaTypeResolver implements TypeResolver {


    private final PsiClass psiClass;
    private final TypeDefinition targetType;

    public IdeaJavaTypeResolver(PsiClass psiClass, TypeDefinition targetType) {
        this.psiClass = psiClass;
        this.targetType = targetType;
    }


    @Override
    public Map<String, Setter> getInstantSetters() {
        Map<String, Setter> result = new HashMap<>();
        PsiMethod[] allMethods = psiClass.getAllMethods();
        for (PsiMethod method : allMethods) {
            String setterVarName = getSetterVarName(method);
            if (setterVarName == null) {
                continue;
            }
            JvmParameter parameter = method.getParameters()[0];

            Setter setter = new Setter();
            setter.setVarName(setterVarName);
            setter.setMethodName(method.getName());
            //setter.setType();
            result.put(setterVarName, setter);
        }


        return result;
    }


    @Override
    public List<Constructor> getConstructors() {
        return null;
    }

    @Override
    public Map<String, Getter> getInstantGetters() {
        Map<String, Getter> result = new HashMap<>();
        PsiMethod[] allMethods = psiClass.getAllMethods();
        for (PsiMethod method : allMethods) {
            String getterVarName = getGetterVarName(method);
            if (getterVarName == null) {
                continue;
            }
            PsiType returnType = method.getReturnType();
            Getter getter = new Getter();
            getter.setVarName(getterVarName);
            getter.setMethodName(method.getName());
            //setter.setType();
            result.put(getterVarName, getter);
        }
        return result;
    }

    @Override
    public TypeDefinition getTypeDefinition() {
        return targetType;
    }

    private String getSetterVarName(PsiMethod method) {
        if (method.getParameters().length != 1) {
            return null;
        }
        String name = method.getName();
        if (!name.startsWith("set")) {
            return null;
        }
        return varNameHandle(name.substring(3));
    }

    private String getGetterVarName(PsiMethod method) {
        if (method.getParameters().length != 0) {
            return null;
        }
        String name = method.getName();
        if (!name.startsWith("get")) {
            return null;
        }
        return varNameHandle(name.substring(3));
    }


    private String varNameHandle(String data) {
        if (data == null || data.length() < 1) {
            return null;
        }

        char[] chars = data.toCharArray();
        chars[0] = toLow(chars[0]);
        return new String(chars);
    }

    private char toLow(char c) {
        if (c >= 'A' && c <= 'Z') {
            c += 32;
        }
        return c;
    }

}
