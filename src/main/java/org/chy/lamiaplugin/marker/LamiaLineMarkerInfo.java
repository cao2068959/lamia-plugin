package org.chy.lamiaplugin.marker;

import cn.hutool.core.util.RandomUtil;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;

import com.intellij.openapi.editor.Document;

import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;

import com.intellij.openapi.util.IconLoader;

import com.intellij.psi.*;


import org.chy.lamiaplugin.exception.LamiaException;
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
        private int methodLen;

        private final boolean isComplete;
        private final T element;
        private final Document document;
        private LamiaLineMarkerInfo<T> lineMarkerInfo;

        public Handler(T element,
                       PsiMethodCallExpression lamiaMethod, boolean isComplete) {
            this.element = element;
            this.project = element.getProject();
            updateLamiaMethod(lamiaMethod);
            this.isComplete = isComplete;

            PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
            this.document = documentManager.getDocument(element.getContainingFile());
        }


        public GutterIconNavigationHandler<T> getNavigationHandler() {
            return (event, psiElement) -> {
                LamiaLineMarkerHandler handler = LamiaLineMarkerHandler.of(project);
                PsiMethodCallExpression method;
                try {
                    method = getLamiaMethod();
                } catch (Exception e) {
                    handler.showFailMsg(event, "The current expression ref is invalid：" + e.getMessage());
                    return;
                }
                handler.click(event, method, isComplete);
            };
        }


        public PsiMethodCallExpression getLamiaMethod() {
            if (this.lamiaMethod.isValid()) {
                if (this.lamiaMethod.getTextLength() != methodLen) {
                    updateLamiaMethod(PsiMethodUtils.getLamiaStartExpression(lamiaMethod));
                }
                return this.lamiaMethod;
            }
            PsiMethodCallExpression methodCall = PsiMethodUtils.getMethodCall(lineMarkerInfo.getElement());
            methodCall = PsiMethodUtils.getLamiaStartExpression(methodCall);
            if (methodCall == null) {
                throw new LamiaException("The current expression has expired, but a new expression reference cannot be found \n " +
                        "If there must be a bug, please contact the author for modification\n" +
                        "\n" +
                        "QQ: 704188931");
            }
            updateLamiaMethod(methodCall);
            return lamiaMethod;
        }

        private void updateLamiaMethod(PsiMethodCallExpression expression) {
            if (expression == null) {
                return;
            }

            this.lamiaMethod = expression;
            this.methodLen = expression.getTextLength();
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
