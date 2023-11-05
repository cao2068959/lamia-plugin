package org.chy.lamiaplugin;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.augment.PsiAugmentProvider;
import com.intellij.psi.augment.PsiExtensionMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LamiaAugmentProvider extends PsiAugmentProvider {

    @Override
    protected @NotNull <Psi extends PsiElement> List<Psi> getAugments(@NotNull PsiElement element, @NotNull Class<Psi> type, @Nullable String nameHint) {
        return super.getAugments(element, type, nameHint);
    }

    @Override
    protected List<PsiExtensionMethod> getExtensionMethods(@NotNull PsiClass aClass, @NotNull String nameHint, @NotNull PsiElement context) {
        return super.getExtensionMethods(aClass, nameHint, context);
    }
}
