package org.chy.lamiaplugin;

import com.intellij.find.findUsages.CustomUsageSearcher;
import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.psi.PsiElement;
import com.intellij.slicer.JavaSliceUsage;
import com.intellij.slicer.SliceAnalysisParams;
import com.intellij.usages.Usage;
import com.intellij.util.Processor;
import org.chy.lamiaplugin.marker.LamiaLineMarkerHandler;
import org.jetbrains.annotations.NotNull;

public class LamiaCustomUsageSearcher extends CustomUsageSearcher {
    @Override
    public void processElementUsages(@NotNull PsiElement element, @NotNull Processor<? super Usage> processor,
                                     @NotNull FindUsagesOptions options) {


        JavaSliceUsage rootUsage = JavaSliceUsage.createRootUsage(LamiaLineMarkerHandler.psiElement, new SliceAnalysisParams());
        processor.process(rootUsage);
        System.out.println(rootUsage);
    }
}
