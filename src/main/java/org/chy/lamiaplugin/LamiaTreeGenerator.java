package org.chy.lamiaplugin;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.impl.source.tree.TreeGenerator;
import com.intellij.util.CharTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LamiaTreeGenerator implements TreeGenerator {
    @Override
    public @Nullable TreeElement generateTreeFor(@NotNull PsiElement original, @NotNull CharTable table, @NotNull PsiManager manager) {
        System.out.println("------> tree 生成");
        return null;
    }


}
