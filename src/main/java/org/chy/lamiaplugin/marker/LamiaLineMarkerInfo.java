package org.chy.lamiaplugin.marker;

import cn.hutool.core.util.RandomUtil;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;

import com.intellij.openapi.editor.Document;

import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;

import com.intellij.openapi.util.IconLoader;

import com.intellij.psi.*;


import org.chy.lamiaplugin.utlis.PsiMethodUtils;

import javax.swing.*;


public class LamiaLineMarkerInfo<T extends PsiElement> extends LineMarkerInfo<T> {


    static Icon LAMIA_ICON = IconLoader.getIcon("/images/img_1.png", LamiaLineMarkerInfo.class);

    static Icon LAMIA_ICON2 = IconLoader.getIcon("/images/img_2.png", LamiaLineMarkerInfo.class);


    public LamiaLineMarkerInfo(Handler<T> handler) {
        super(handler.element, handler.element.getTextRange(),
                handler.isComplete ? LAMIA_ICON : LAMIA_ICON2, (data) -> "Lamia转换语句",
                handler.getNavigationHandler(),
                GutterIconRenderer.Alignment.CENTER, () -> "LamiaMarkerInfo");
        handler.setLineMarkerInfo(this);
    }


    public static class Handler<T extends PsiElement> {
        private final Project project;
        private PsiMethodCallExpression lamiaMethod;
        private final boolean isComplete;
        private final T element;
        private final Document document;
        private LamiaLineMarkerInfo<T> lineMarkerInfo;

        public Handler(T element,
                       PsiMethodCallExpression lamiaMethod, boolean isComplete) {
            this.element = element;
            this.project = element.getProject();
            this.lamiaMethod = lamiaMethod;
            this.isComplete = isComplete;

            PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
            this.document = documentManager.getDocument(element.getContainingFile());
        }


        public GutterIconNavigationHandler<T> getNavigationHandler() {
            return (event, psiElement) -> {
                PsiMethodCallExpression method = getLamiaMethod();
                LamiaLineMarkerHandler handler = LamiaLineMarkerHandler.of(method.getProject());
                handler.click(event, method, isComplete);
            };
        }

        public PsiMethodCallExpression getLamiaMethod() {
            if (lamiaMethod.isValid()) {
                return lamiaMethod;
            }
            PsiMethodCallExpression lamiaMethodByOffset = getLamiaMethodByOffset(lineMarkerInfo.startOffset);
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

        public void setLineMarkerInfo(LamiaLineMarkerInfo<T> lineMarkerInfo) {
            this.lineMarkerInfo = lineMarkerInfo;
        }
    }


}
