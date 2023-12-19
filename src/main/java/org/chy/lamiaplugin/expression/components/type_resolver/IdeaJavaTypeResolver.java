package org.chy.lamiaplugin.expression.components.type_resolver;

import com.chy.lamia.convert.core.components.TypeResolver;
import com.chy.lamia.convert.core.components.TypeResolverFactory;
import com.chy.lamia.convert.core.entity.*;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
            JvmParameter[] parameters = method.getParameters();
            if (parameters.length != 1) {
                continue;
            }
            JvmParameter parameter = method.getParameters()[0];
            Setter setter = new Setter();
            setter.setVarName(setterVarName);
            setter.setMethodName(method.getName());
            setter.setType(new TypeDefinition(convertType(parameter.getType()).getCanonicalText()));
            result.put(setterVarName, setter);
        }


        return result;
    }


    @Override
    public List<Constructor> getConstructors() {
        PsiMethod[] constructors = psiClass.getConstructors();
        List<Constructor> result = new ArrayList<>();
        for (PsiMethod constructor : constructors) {
            Constructor cs = new Constructor();
            cs.setParams(toVarDefinitions(constructor.getParameters()));
            result.add(cs);
        }

        return result;
    }

    private List<VarDefinition> toVarDefinitions(JvmParameter[] parameters) {
        if (parameters == null || parameters.length == 0) {
            return new ArrayList<>();
        }
        List<VarDefinition> result = new ArrayList<>(parameters.length);
        for (JvmParameter parameter : parameters) {
            TypeDefinition typeDefinition = new TypeDefinition(convertType(parameter.getType()).getCanonicalText());
            VarDefinition varDefinition = new VarDefinition(parameter.getName(), typeDefinition);
            result.add(varDefinition);
        }
        return result;
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
            if (returnType == null) {
                continue;
            }

            Getter getter = new Getter();
            getter.setVarName(getterVarName);
            getter.setMethodName(method.getName());
            getter.setType(new TypeDefinition(returnType.getCanonicalText()));
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

    public PsiType convertType(@NotNull JvmType type) {
        if (type instanceof PsiType) return (PsiType) type;
        throw new RuntimeException("TODO");
    }
}
