package org.chy.lamiaplugin.marker;

import cn.hutool.core.util.RandomUtil;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;

import com.intellij.openapi.editor.Document;

import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;

import com.intellij.openapi.util.IconLoader;

import com.intellij.psi.*;


import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class LamiaLineMarkerInfo<T extends PsiElement> extends LineMarkerInfo<T> {


    static Icon LAMIA_ICON = IconLoader.getIcon("/images/img_1.png", LamiaLineMarkerInfo.class);

    static Icon LAMIA_ICON2 = IconLoader.getIcon("/images/img_2.png", LamiaLineMarkerInfo.class);
    private final Project project;
    private final Document document;


    private T lamiaMethod;


    public LamiaLineMarkerInfo(@NotNull T element, T lamiaMethod) {
        super(element, element.getTextRange(),
                LAMIA_ICON, (data) -> "Lamia转换语句",
                null,
                GutterIconRenderer.Alignment.CENTER, () -> "LamiaMarkerInfo");
        this.lamiaMethod = lamiaMethod;
        this.project = lamiaMethod.getProject();
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        this.document = documentManager.getDocument(element.getContainingFile());
    }

    public GutterIconNavigationHandler<T> getNavigationHandler() {
        return (event, psiElement) -> {
            T method = getLamiaMethod();
            LamiaLineMarkerHandler handler = LamiaLineMarkerHandler.of(method.getProject());
            handler.click(event, method);
        };
    }

    @Override
    public Icon getIcon() {
        return super.getIcon();
    }

    public void setLamiaMethod(T lamiaMethod) {
        this.lamiaMethod = lamiaMethod;
    }

    public T getLamiaMethod() {
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        PsiFile psiFile = psiDocumentManager.getPsiFile(document);
        PsiElement element = psiFile.findElementAt(startOffset);
        return lamiaMethod;
    }

}
