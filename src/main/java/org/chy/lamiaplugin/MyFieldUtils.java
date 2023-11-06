package org.chy.lamiaplugin;
 
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.light.LightFieldBuilder;
import com.intellij.psi.impl.light.LightMethodBuilder;
import com.intellij.psi.impl.light.LightModifierList;

import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ObjectUtils;
 
import java.util.List;
 
public class MyFieldUtils {
    static public void addField(PsiJavaFile psiJavaFile,String annotaionName){
 
        PsiClass[] psiClasses = psiJavaFile.getClasses();
        if (null == psiClasses) {
            return;
        }
        for(PsiClass psiClass : psiClasses) {
            PsiAnnotation psiAnnotation = psiClass.getAnnotation(annotaionName);
            if (psiAnnotation == null) {
                continue;
            }
            String fieldName = getAnnotationValue(psiAnnotation,"fieldName");
 
            if ("".equals(fieldName) == true) {
                break;
            }
 
            boolean add = haveField(psiClass,fieldName);
            if (add) {
                createField(psiClass,fieldName);
            }
 
 
            boolean method =true;
            if (method){
                String methodName = "get" + fieldName;
                PsiType returnType = getPsiType(psiClass,"java.lang.String");
                add = haveMethod(psiClass,methodName,null,returnType);
                if (add) {
                    createMethod(psiClass,methodName,null,returnType);
                }
            }
 
            break;
 
        }
    }
 
    static public boolean haveAnnotation(PsiJavaFile psiJavaFile,String annotaionName){
        boolean bRet = false;
        PsiClass[] psiClasses = psiJavaFile.getClasses();
        if (null == psiClasses) {
            return bRet;
        }
        for(PsiClass psiClass : psiClasses) {
            PsiAnnotation psiAnnotation = getAnnotation(psiClass,annotaionName);
            if (psiAnnotation != null){
                bRet = true;
                break;
            }
        }
        return bRet;
    }
 
    static public PsiAnnotation getAnnotation(PsiClass psiClass,String annotaionName){
        PsiAnnotation psiAnnotation = psiClass.getAnnotation(annotaionName);
        return psiAnnotation;
    }
 
    static public PsiField createField(PsiClass psiClass,String fieldName){
        Project project = psiClass.getProject();
        PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(project);
        PsiType stringPsiType = psiElementFactory.createTypeFromText("java.lang.String", null);
        PsiField psiField = psiElementFactory.createField(fieldName, stringPsiType);
        psiField.getModifierList().setModifierProperty(PsiModifier.PUBLIC,true);
 
        WriteCommandAction.runWriteCommandAction(project, new Runnable() {
            @Override
            public void run() {
                psiClass.add(psiField);
            }
        });
 
        return psiField;
    }
    
    static public PsiField createCacheField(PsiClass psiClass,String fieldName,PsiType psiType){
        PsiManager psiManager = psiClass.getContainingFile().getManager();
        LightFieldBuilder lightFieldBuilder = new LightFieldBuilder(psiManager,fieldName, psiType);
 
        LightModifierList lightModifierList = (LightModifierList) lightFieldBuilder.getModifierList();
        lightModifierList.addModifier(PsiModifier.PUBLIC);
 
        lightFieldBuilder.setContainingClass(psiClass);
 
        return lightFieldBuilder;
    }
 
    static public String getAnnotationValue(PsiAnnotation psiAnnotation,String name){
        String value = null;
        PsiAnnotationParameterList psiAnnotationParameterList = psiAnnotation.getParameterList();
        for (PsiNameValuePair psiNameValuePair : psiAnnotationParameterList.getAttributes()) {
            String attributeName = psiNameValuePair.getAttributeName();
 
            if (attributeName.equals(name) == false) {
                continue;
            }
            value = psiNameValuePair.getLiteralValue();
            break;
        }
        return value;
    }
 
    static public boolean haveField(PsiClass psiClass,String fieldName){
        boolean bRet = false;
        PsiField[] psiFields = psiClass.getAllFields();
        PsiField lastPsiField = null;
        for (PsiField psiField : psiFields) {
            if (fieldName.equals(psiField.getName())) {
                bRet = true;
                break;
            }
        }
        return bRet;
    }
 
    static public boolean haveMethod(PsiClass psiClass,String methodName,List<PsiParameter> parameterList,PsiType returnType){
        boolean bRet = false;
        PsiMethod[] psiMethods = psiClass.getAllMethods();
        for (PsiMethod psiMethod : psiMethods) {
            if (methodName.equals(psiMethod.getName())) {
                PsiType psiType = psiMethod.getReturnType();
                if (psiType.equals(returnType) == false){
                    return bRet;
                }
                PsiParameterList psiParameterList = psiMethod.getParameterList();
 
                if (ObjectUtils.isEmpty(parameterList)){
                    parameterList = Lists.newArrayList();
                }
 
                if (parameterList.size() != psiParameterList.getParameters().length){
                    return bRet;
                }
 
                int i = 0;
                for(PsiParameter parameter : parameterList){
                    PsiParameter psiParameter = psiParameterList.getParameter(i);
                    if (parameter.getName().equals(psiParameter.getName()) == false){
                        return bRet;
                    }
 
                    if (parameter.getType().equals(psiParameter.getType()) == false){
                        return bRet;
                    }
 
                    i++;
 
                }
                bRet = true;
                break;
            }
        }
        return bRet;
    }
 
    static public PsiMethod createMethod(PsiClass psiClass,String methodName,List<PsiParameter> parameterList,PsiType returnType){
        Project project = psiClass.getProject();
        PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(project);
 
        PsiMethod psiMethod = psiElementFactory.createMethod(methodName, returnType);
        psiMethod.getModifierList().setModifierProperty(PsiModifier.PUBLIC,true);
 
        WriteCommandAction.runWriteCommandAction(project, new Runnable() {
            @Override
            public void run() {
                psiClass.add(psiMethod);
            }
        });
 
        return psiMethod;
    }
 
    static public PsiMethod createCacheMethod(PsiClass psiClass,String methodName,List<PsiParameter> parameterList,PsiType returnType) {
        PsiManager psiManager = psiClass.getContainingFile().getManager();
 
        LightMethodBuilder lightMethodBuilder = new LightMethodBuilder(psiManager, JavaLanguage.INSTANCE, methodName);
        lightMethodBuilder.addModifier(PsiModifier.PUBLIC);
        lightMethodBuilder.setContainingClass(psiClass);
        if (ObjectUtils.isNotEmpty(parameterList)) {
            for (PsiParameter psiParameter : parameterList) {
                lightMethodBuilder.addParameter(psiParameter.getName(), psiParameter.getType());
            }
        }
        lightMethodBuilder.setMethodReturnType(returnType);
        lightMethodBuilder.setContainingClass(psiClass);
 
        return lightMethodBuilder;
    }
 
    static public PsiType getPsiType(PsiClass psiClass,String type){
        Project project = psiClass.getProject();
        PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(project);
        PsiType psiType = psiElementFactory.createTypeFromText(type, null);
        return psiType;
    }
}