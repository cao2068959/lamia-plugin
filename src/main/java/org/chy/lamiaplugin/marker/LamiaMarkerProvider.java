package org.chy.lamiaplugin.marker;


import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;

import com.intellij.psi.*;

import org.jetbrains.annotations.NotNull;


import static org.chy.lamiaplugin.utlis.PsiMethodUtils.isLamiaExpressionMethodCall;


public class LamiaMarkerProvider implements LineMarkerProvider {



    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {

        if (!(element instanceof PsiMethodCallExpression methodCallExpression)) {
            return null;
        }

        if (!isLamiaExpressionMethodCall(methodCallExpression)) {
            return null;
        }

        PsiElement leafElement = element.getFirstChild().getFirstChild().getLastChild();
        return new LamiaLineMarkerInfo<>(leafElement, element);
    }







}
