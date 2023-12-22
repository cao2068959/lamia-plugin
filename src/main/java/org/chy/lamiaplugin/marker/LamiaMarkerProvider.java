package org.chy.lamiaplugin.marker;

import com.chy.lamia.convert.core.annotation.LamiaMapping;
import com.chy.lamia.expose.Lamia;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import org.jetbrains.annotations.NotNull;


public class LamiaMarkerProvider implements LineMarkerProvider {

    private static final String LAMIA_PATH = Lamia.class.getName();

    private static final String LAMIA_ANNOTATION_PATH = LamiaMapping.class.getName();


    private static final String LAMIA_SHORT_PATH = "Lamia";


    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {

        if (!(element instanceof PsiMethodCallExpression methodCallExpression)) {
            return null;
        }

        if (!isLamiaExpression(methodCallExpression)) {
            return null;
        }

        PsiElement leafElement = element.getFirstChild().getFirstChild().getLastChild();
        return new LamiaLineMarkerInfo<>(leafElement, element);
    }


    private boolean isLamiaExpression(PsiMethodCallExpression element) {
        PsiElement qualifier = element.getMethodExpression().getQualifier();
        if (!(qualifier instanceof PsiJavaCodeReferenceElement javaQualifier)) {
            return false;
        }

        if (!qualifier.getText().endsWith(LAMIA_SHORT_PATH)) {
            return false;
        }

        if (!LAMIA_PATH.equals(javaQualifier.getQualifiedName())) {
            return false;
        }

        // 检查一下是否有注解
        PsiMethod belongMethod = getBelongMethod(element);
        if (belongMethod == null) {
            return false;
        }
        PsiAnnotation annotation = belongMethod.getAnnotation(LAMIA_ANNOTATION_PATH);


        return annotation != null;

    }

    private PsiMethod getBelongMethod(PsiElement psiElement) {

        PsiElement data = psiElement.getParent();
        while (true) {

            if (data instanceof PsiMethod method) {
                return method;
            }
            if (data == null || data instanceof PsiClass || data instanceof PsiFile) {
                return null;
            }
            data = data.getParent();
        }
    }


}
