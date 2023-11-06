package org.chy.lamiaplugin;

import com.intellij.codeInsight.daemon.ImplicitUsageProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class LamiaImplicitUsageProvider implements ImplicitUsageProvider {


    @Override
    public boolean isImplicitUsage(@NotNull PsiElement element) {
        return true;
    }

    @Override
    public boolean isImplicitRead(@NotNull PsiElement element) {
        return true;
    }

    @Override
    public boolean isImplicitWrite(@NotNull PsiElement element) {
        return true;
    }
}
