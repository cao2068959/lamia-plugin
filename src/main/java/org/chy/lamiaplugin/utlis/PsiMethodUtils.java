package org.chy.lamiaplugin.utlis;

import com.chy.lamia.convert.core.annotation.LamiaMapping;
import com.chy.lamia.expose.Lamia;
import com.intellij.psi.*;

public class PsiMethodUtils {

    private static final String LAMIA_PATH = Lamia.class.getName();

    private static final String LAMIA_ANNOTATION_PATH = LamiaMapping.class.getName();


    private static final String LAMIA_SHORT_PATH = "Lamia";

    public static void getLamiaMethod(PsiElement psiElement) {

    }

    public static PsiMethod getBelongMethod(PsiElement psiElement) {
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

    public static boolean isLamiaExpression(PsiElement psiElement) {
        PsiMethodCallExpression methodCallExpression = getRecentlyMethodCallExpression(psiElement);
        if (methodCallExpression == null) {
            return false;
        }
        String text = methodCallExpression.getText();
        if (!text.startsWith(LAMIA_SHORT_PATH)) {
            return false;
        }
        PsiMethod belongMethod = getBelongMethod(psiElement);
        if (belongMethod == null) {
            return false;
        }
        PsiAnnotation annotation = belongMethod.getAnnotation(LAMIA_ANNOTATION_PATH);
        return annotation != null;
    }

    /**
     * 向下找获取最近一层的 PsiMethodCallExpression
     *
     * @param psiElement
     * @return
     */
    public static PsiMethodCallExpression getRecentlyMethodCallExpression(PsiElement psiElement) {
        if (psiElement instanceof PsiMethodCallExpression methodCallExpression) {
            return methodCallExpression;
        }
        PsiElement[] children = psiElement.getChildren();

        for (PsiElement child : children) {
            if (child instanceof PsiMethodCallExpression methodCallExpression) {
                return methodCallExpression;
            }
        }
        return null;
    }

    public static PsiElement getCompleteExpression(PsiElement refExpr) {
        PsiElement result = refExpr;

        while (true) {
            if (result == null) {
                return null;
            }
            PsiElement parent = result.getParent();
            if (parent instanceof PsiCodeBlock) {
                return null;
            }
            if (parent instanceof PsiTypeCastExpression) {
                return parent;
            }

            if (parent instanceof PsiStatement) {
                return result;
            }
            result = parent;
        }
    }

    /**
     * 判断是不是 Lamia的表达式的直接调用， 只有 Lamia.xxx() 这种才算
     */
    public static boolean isLamiaExpressionMethodCall(PsiMethodCallExpression element) {
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

}
