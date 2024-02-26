package org.chy.lamiaplugin.marker;


import com.chy.lamia.convert.core.entity.LamiaConvertInfo;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;

import com.intellij.openapi.diagnostic.Logger;

import com.intellij.psi.*;

import org.chy.lamiaplugin.expression.LamiaExpressionManager;
import org.jetbrains.annotations.NotNull;


import static org.chy.lamiaplugin.utlis.PsiMethodUtils.isLamiaExpressionMethodCall;


public class LamiaMarkerProvider implements LineMarkerProvider {

    private static final Logger LOG = Logger.getInstance(LamiaMarkerProvider.class);

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {

        if (!element.isValid()) {
            return null;
        }


        if (!(element instanceof PsiMethodCallExpression methodCallExpression)) {
            return null;
        }

        if (!isLamiaExpressionMethodCall(methodCallExpression)) {
            return null;
        }

        boolean isComplete = isCompleteExpression(methodCallExpression);
        PsiElement leafElement = element.getFirstChild().getFirstChild().getLastChild();

        return new LamiaLineMarkerInfo<>(new LamiaLineMarkerInfo.Handler<>(leafElement, methodCallExpression, isComplete));
    }

    private boolean isCompleteExpression(PsiMethodCallExpression methodCallExpression) {
        LamiaExpressionManager manager = LamiaExpressionManager.getInstance(methodCallExpression.getProject());
        LamiaConvertInfo lamiaConvertInfo = manager.resolvingExpression(methodCallExpression, e -> {
            LOG.warn("解析lamia表达式失败", e);
        });
        if (lamiaConvertInfo == null) {
            return false;
        }
        return lamiaConvertInfo.isCompleteConvert();
    }

}
