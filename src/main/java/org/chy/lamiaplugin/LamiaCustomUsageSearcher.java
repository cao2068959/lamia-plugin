package org.chy.lamiaplugin;

import com.intellij.find.findUsages.CustomUsageSearcher;
import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.find.usages.api.PsiUsage;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.slicer.JavaSliceUsage;
import com.intellij.slicer.SliceAnalysisParams;
import com.intellij.usages.Usage;
import com.intellij.util.Processor;
import org.chy.lamiaplugin.expression.LamiaExpressionManager;
import org.chy.lamiaplugin.expression.entity.RelationClassWrapper;
import org.chy.lamiaplugin.marker.LamiaLineMarkerHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class LamiaCustomUsageSearcher extends CustomUsageSearcher {
    @Override
    public void processElementUsages(@NotNull PsiElement element, @NotNull Processor<? super Usage> processor,
                                     @NotNull FindUsagesOptions options) {

        if (!(element instanceof PsiField field)) {
            return;
        }

        ApplicationManager.getApplication().runReadAction(() -> {
            genRelationUsage(field, processor);
        });


    }

    private void genRelationUsage(PsiField field, Processor<? super Usage> processor) {
        PsiClass containingClass = field.getContainingClass();
        if (containingClass == null) {
            return;
        }

        String className = containingClass.getQualifiedName();
        if (className == null) {
            return;
        }
        String fieldName = field.getName();
        LamiaExpressionManager manager = LamiaExpressionManager.getInstance(field.getProject());


        Set<RelationClassWrapper> allRelations = manager.getRelationLamia(className);
        for (RelationClassWrapper relation : allRelations) {
            if (!relation.isContainFiled(fieldName)) {
                continue;
            }
            PsiMethodCallExpression expression = relation.getLamiaExpression().getExpression();
            if (!expression.isValid()) {
                continue;
            }
            // 如果包含了对应的表达式，那么显示
            JavaSliceUsage usageInfo = JavaSliceUsage.createRootUsage(expression, new SliceAnalysisParams());
            processor.process(usageInfo);
        }
    }
}

