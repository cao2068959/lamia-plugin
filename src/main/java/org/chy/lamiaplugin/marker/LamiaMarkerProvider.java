package org.chy.lamiaplugin.marker;


import cn.hutool.core.util.ReflectUtil;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;

import com.intellij.codeInsight.daemon.impl.LineMarkersUtil;
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


        PsiElement leafElement = element.getFirstChild().getFirstChild().getLastChild();
        LamiaLineMarkerInfo<PsiElement> marker = new LamiaLineMarkerInfo<>(leafElement, element);


        Project project = element.getProject();

        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        Document document = documentManager.getDocument(element.getContainingFile());

        MarkupModelEx markupModel = (MarkupModelEx) DocumentMarkupModel.forDocument(document, project, true);
        markupModel.processRangeHighlightersOverlappingWith(marker.startOffset, marker.endOffset,
                highlighter -> {
                    Key<LineMarkerInfo<?>> lineMarkerInfo1 = (Key<LineMarkerInfo<?>>) ReflectUtil.getStaticFieldValue(ReflectUtil.getField(LineMarkersUtil.class, "LINE_MARKER_INFO"));

                    // 获取 LineMarkerInfo
                    LineMarkerInfo<PsiElement> info = (LineMarkerInfo<PsiElement>) highlighter.getUserData(lineMarkerInfo1);

                    if (info instanceof LamiaLineMarkerInfo<PsiElement> lineMarkerInfo) {
                        lineMarkerInfo.setLamiaMethod(element);
                    }
                    return true;
                });
        return marker;
    }

}
