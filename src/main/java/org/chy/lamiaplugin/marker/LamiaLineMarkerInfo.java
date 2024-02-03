package org.chy.lamiaplugin.marker;

import cn.hutool.core.util.RandomUtil;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;

import com.intellij.openapi.editor.Document;

import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;

import com.intellij.openapi.util.IconLoader;

import com.intellij.psi.*;


import com.siyeh.ig.psiutils.MethodCallUtils;
import org.chy.lamiaplugin.utlis.PsiMethodUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class LamiaLineMarkerInfo<T extends PsiElement> extends LineMarkerInfo<T> {


    static Icon LAMIA_ICON = IconLoader.getIcon("/images/img_1.png", LamiaLineMarkerInfo.class);

    static Icon LAMIA_ICON2 = IconLoader.getIcon("/images/img_2.png", LamiaLineMarkerInfo.class);
    private final Project project;
    private final Document document;

    /**
     * 表达式不完整有缺失
     */
    private final boolean isComplete;


    private PsiMethodCallExpression lamiaMethod;


    public LamiaLineMarkerInfo(@NotNull T element, PsiMethodCallExpression lamiaMethod, boolean isComplete) {
        super(element, element.getTextRange(),
                isComplete ? LAMIA_ICON : LAMIA_ICON2, (data) -> "Lamia转换语句",
                null,
                GutterIconRenderer.Alignment.CENTER, () -> "LamiaMarkerInfo");
        this.lamiaMethod = lamiaMethod;
        this.project = lamiaMethod.getProject();
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        this.document = documentManager.getDocument(element.getContainingFile());
        this.isComplete = isComplete;
    }

    public GutterIconNavigationHandler<T> getNavigationHandler() {
        return (event, psiElement) -> {
            PsiMethodCallExpression method = getLamiaMethod();
            LamiaLineMarkerHandler handler = LamiaLineMarkerHandler.of(method.getProject());
            handler.click(event, method, isComplete);
        };
    }

    @Override
    public Icon getIcon() {
        return super.getIcon();
    }

    public void setLamiaMethod(PsiMethodCallExpression lamiaMethod) {
        this.lamiaMethod = lamiaMethod;
    }

    public PsiMethodCallExpression getLamiaMethod() {
        if (lamiaMethod.isValid()) {
            return lamiaMethod;
        }
        PsiMethodCallExpression lamiaMethodByOffset = getLamiaMethodByOffset(this.startOffset);
        if (lamiaMethodByOffset != null) {
            lamiaMethod = lamiaMethodByOffset;
            return lamiaMethodByOffset;
        }
        return lamiaMethod;
    }

    private PsiMethodCallExpression getLamiaMethodByOffset(int offset) {
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        PsiFile psiFile = psiDocumentManager.getPsiFile(document);
        PsiElement elementAt = psiFile.findElementAt(offset);
        return PsiMethodUtils.getMethodCall(elementAt);
    }


}
