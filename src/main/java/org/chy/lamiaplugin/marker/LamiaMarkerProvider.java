package org.chy.lamiaplugin.marker;


import cn.hutool.core.util.ReflectUtil;
import com.chy.lamia.convert.core.entity.LamiaConvertInfo;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;

import com.intellij.codeInsight.daemon.impl.LineMarkersUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.ex.MarkupModelEx;
import com.intellij.openapi.editor.impl.DocumentMarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.*;

import org.chy.lamiaplugin.expression.ConvertResult;
import org.chy.lamiaplugin.expression.LamiaExpressionManager;
import org.jetbrains.annotations.NotNull;


import java.util.Collection;
import java.util.List;

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
        LamiaLineMarkerInfo<PsiElement> marker = new LamiaLineMarkerInfo<>(leafElement, methodCallExpression, isComplete);
        return marker;
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
