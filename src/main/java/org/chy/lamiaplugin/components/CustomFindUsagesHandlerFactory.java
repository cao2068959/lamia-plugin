package org.chy.lamiaplugin.components;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesHandlerFactory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.NotNull;

public class CustomFindUsagesHandlerFactory extends FindUsagesHandlerFactory {
    @Override
    public boolean canFindUsages(@NotNull PsiElement element) {
        // 只处理 PsiField
        return element instanceof PsiField;
    }

    @Override
    public FindUsagesHandler createFindUsagesHandler(@NotNull PsiElement element, boolean forHighlightUsages) {
        return new FindUsagesHandler(element) {
            @NotNull
            @Override
            public PsiElement[] getSecondaryElements() {
                // 创建你自定义的 PsiElement
                PsiElement customElement = createCustomElement();
                return new PsiElement[]{customElement};
            }
        };
    }

    private PsiElement createCustomElement() {
        // 这里是创建你自定义的 PsiElement 的代码，具体实现根据你的需求来定
        return null;
    }
}