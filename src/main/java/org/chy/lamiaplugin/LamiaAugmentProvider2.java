package org.chy.lamiaplugin;

import com.google.common.collect.Lists;
import com.intellij.psi.*;
import com.intellij.psi.augment.PsiAugmentProvider;
import com.intellij.psi.impl.light.LightParameter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LamiaAugmentProvider2 extends PsiAugmentProvider {


    @Override
    @NotNull
    protected <Psi extends PsiElement> List<Psi> getAugments(@NotNull PsiElement psiElement, @NotNull Class<Psi> type) {
        final List<Psi> emptyResult = Collections.emptyList();

        if (!(psiElement instanceof PsiClass)) {
            return emptyResult;
        }

        PsiClass psiClass = (PsiClass) psiElement;


        if (PsiField.class.isAssignableFrom(type)) {
            String fieldName = "chy";
            PsiType psiType = MyFieldUtils.getPsiType(psiClass, "java.lang.String");
            PsiField psiField = MyFieldUtils.createCacheField(psiClass, fieldName, psiType);
            List<PsiElement> psiElementList = new ArrayList<>();
            psiElementList.add(psiField);

            return (List<Psi>) psiElementList;
        } else if (PsiMethod.class.isAssignableFrom(type)) {
            String fieldName = "chy";
            String methed = "addChy";
            boolean addMethod = true;
            if (StringUtils.isNotBlank(methed)) {
                addMethod = false;
            }

            if (!"".equals(fieldName)) {
                List<PsiElement> psiElementList = new ArrayList<>();
                if (addMethod) {
                    PsiType psiType = MyFieldUtils.getPsiType(psiClass, "java.lang.String");
                    String methodName = "get" + fieldName;
                    //PsiType.VOID
                    PsiMethod psiMethod = MyFieldUtils.createCacheMethod(psiClass, methodName, null, psiType);
                    psiElementList.add(psiMethod);

                    methodName = "set" + fieldName;
                    List<PsiParameter> parameterList = Lists.newArrayList();
                    LightParameter lightParameter = new LightParameter(fieldName, psiType, psiClass);
                    parameterList.add(lightParameter);
                    psiMethod = MyFieldUtils.createCacheMethod(psiClass, methodName, parameterList, PsiType.VOID);
                    psiElementList.add(psiMethod);

                    return (List<Psi>) psiElementList;
                }
            }
        }
        return emptyResult;
    }

}
