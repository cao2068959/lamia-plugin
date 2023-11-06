package org.chy.lamiaplugin;

import com.google.common.collect.Lists;
import com.intellij.psi.*;
import com.intellij.psi.augment.PsiAugmentProvider;
import com.intellij.psi.augment.PsiExtensionMethod;
import com.intellij.psi.impl.light.LightParameter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LamiaAugmentProvider extends PsiAugmentProvider {


    @Override
    @NotNull
    protected <Psi extends PsiElement> List<Psi> getAugments(@NotNull PsiElement psiElement, @NotNull Class<Psi> type) {
        final List<Psi> emptyResult = Collections.emptyList();

        if ((type != PsiClass.class && type != PsiField.class && type != PsiMethod.class)) {
            return emptyResult;
        }

        if (!(psiElement instanceof PsiField psiField)) {
            return emptyResult;
        }


        return emptyResult;
    }

}
