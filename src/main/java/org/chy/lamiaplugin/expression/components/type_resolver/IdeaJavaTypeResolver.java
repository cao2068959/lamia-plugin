package org.chy.lamiaplugin.expression.components.type_resolver;

import com.chy.lamia.convert.core.components.TypeResolver;
import com.chy.lamia.convert.core.entity.*;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.*;
import org.chy.lamiaplugin.utlis.PsiTypeUtils;
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
            PsiParameterList parameterList = method.getParameterList();
            if (parameterList.getParametersCount() != 1) {
                continue;
            }
            PsiParameter parameter = parameterList.getParameter(0);
            Setter setter = new Setter();
            setter.setVarName(setterVarName);
            setter.setMethodName(method.getName());
            setter.setType(PsiTypeUtils.toTypeDefinition(parameter.getType()));
            result.put(setterVarName, setter);
        }


        return result;
    }


    @Override
    public List<Constructor> getConstructors() {
        PsiMethod[] constructors = psiClass.getConstructors();
        List<Constructor> result = new ArrayList<>();
        // 没有构造器，使用一个无参构造器
        if (constructors.length == 0) {
            Constructor cs = new Constructor();
            cs.setParams(new ArrayList<>());
            result.add(cs);
        }

        for (PsiMethod constructor : constructors) {
            Constructor cs = new Constructor();
            cs.setParams(toVarDefinitions(constructor.getParameterList()));
            result.add(cs);
        }

        return result;
    }

    private List<VarDefinition> toVarDefinitions(@NotNull PsiParameterList parameters) {
        if (parameters.isEmpty()) {
            return new ArrayList<>();
        }
        int count = parameters.getParametersCount();
        List<VarDefinition> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            PsiParameter parameter = parameters.getParameter(i);
            TypeDefinition typeDefinition = PsiTypeUtils.toTypeDefinition(parameter.getType());
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
            getter.setType(PsiTypeUtils.toTypeDefinition(returnType));
            result.put(getterVarName, getter);
        }
        return result;
    }

    @Override
    public TypeDefinition getTypeDefinition() {
        return targetType;
    }

    private String getSetterVarName(PsiMethod method) {
        PsiParameterList parameterList = method.getParameterList();
        if (parameterList.getParametersCount() != 1) {
            return null;
        }
        String name = method.getName();
        if (!name.startsWith("set")) {
            return null;
        }
        return varNameHandle(name.substring(3));
    }

    private String getGetterVarName(PsiMethod method) {
        if (!method.getParameterList().isEmpty()) {
            return null;
        }
        String name = method.getName();
        if ("getClass".equals(name)) {
            return null;
        }

        if (!name.startsWith("get")) {
            return null;
        }
        return varNameHandle(name.substring(3));
    }


    private String varNameHandle(String data) {
        if (data == null || data.isEmpty()) {
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
