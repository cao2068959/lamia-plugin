package org.chy.lamiaplugin.utlis;

import com.chy.lamia.convert.core.annotation.LamiaMapping;
import com.chy.lamia.convert.core.utils.struct.Pair;
import com.chy.lamia.expose.Lamia;
import com.chy.lamia.utils.Lists;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.impl.source.tree.TreeUtil;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 获取指定元素所在的代码块的子元素
     *
     * @param element
     * @return
     */
    public static PsiElement getBelongPsiCodeBlockElement(PsiElement element) {
        PsiElement self = element;
        PsiElement parent = self.getParent();
        while (true) {

            if (parent instanceof PsiCodeBlock codeBlock) {
                return self;
            }
            if (parent == null || parent instanceof PsiClass || parent instanceof PsiFile) {
                return null;
            }
            self = parent;
            parent = self.getParent();
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

    public static PsiMethodCallExpression getMethodCallExpressionFromChildren(PsiElement psiElement, int deep) {
        return getMethodCallExpressionFromChildren(psiElement, true, deep);
    }

    public static PsiMethodCallExpression getMethodCallExpressionFromChildren(PsiElement psiElement, boolean returnIfSelfIsMce, int deep) {
        if (returnIfSelfIsMce) {
            if (psiElement instanceof PsiMethodCallExpression methodCallExpression) {
                return methodCallExpression;
            }
        }

        PsiElement[] children = psiElement.getChildren();
        List<PsiElement> next = new ArrayList<>();
        for (PsiElement child : children) {
            if (child instanceof PsiMethodCallExpression methodCallExpression) {
                return methodCallExpression;
            }
            next.add(child);
        }
        if (deep > 0) {
            for (PsiElement psiElement1 : next) {
                PsiMethodCallExpression methodCallExpressionFromChildren = getMethodCallExpressionFromChildren(psiElement1, deep - 1);
                if (methodCallExpressionFromChildren != null) {
                    return methodCallExpressionFromChildren;
                }
            }
        }
        return null;
    }

    /**
     * 判断 psiElement 是否已经准备好了
     *
     * @return
     */
    private static boolean isReady(PsiElement psiElement) {
        if (psiElement.getManager() == null) {
            return false;
        }
        return psiElement.getParent() != null;
    }

    /**
     * 向上找，直到找到方法调用
     *
     * @param psiElement
     * @return
     */
    public static PsiMethodCallExpression getMethodCall(PsiElement psiElement) {
        PsiElement data = psiElement;
        while (true) {
            if (data instanceof PsiMethodCallExpression result) {
                return result;
            }
            if (data == null || data instanceof PsiMethod || data instanceof PsiStatement || data instanceof PsiClass || data instanceof PsiFile) {
                return null;
            }
            data = data.getParent();
        }
    }

    /**
     * 获取 Lamia 的开始表达式 如 Lamia.build() 这样的
     *
     * @param methodCallExpression
     * @return
     */
    public static PsiMethodCallExpression getLamiaStartExpression(PsiMethodCallExpression methodCallExpression) {

        PsiMethodCallExpression nextMethod = methodCallExpression;
        PsiElement cache = methodCallExpression;


        while (true) {
            PsiElement psiElement = getReferenceOrMethodCallByChildren(cache);
            if (psiElement == null) {
                return null;
            }
            cache = psiElement;

            if (psiElement instanceof PsiMethodCallExpression methodCall) {
                nextMethod = methodCall;
                continue;
            }
            String text = psiElement.getText();
            if ("Lamia".equals(text)) {
                return nextMethod;
            }
        }
    }


    public static PsiReferenceExpression getLamiaStartReference(PsiElement psiElement) {
        PsiElement element = psiElement;
        while (true) {
            element = getReferenceOrMethodCallByChildren(element);
            if (element == null) {
                return null;
            }

            if (element instanceof PsiReferenceExpression referenceExpression) {
                String text = element.getText();
                if ("Lamia".equals(text)) {
                    return referenceExpression;
                }
            }

            if (element instanceof PsiCodeBlock || psiElement instanceof PsiLambdaExpression) {
                return null;
            }
        }
    }

    public static PsiElement getReferenceOrMethodCallByChildren(PsiElement psiElement) {
        PsiElement[] children = psiElement.getChildren();
        for (PsiElement child : children) {
            if (child instanceof PsiReferenceExpression result) {
                return result;
            }
            if (child instanceof PsiMethodCallExpression result) {
                return result;
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

    public static List<PsiStatement> getPsiStatement(PsiMethod psiMethod) {
        PsiCodeBlock psiCodeBlock = getPsiCodeBlock(psiMethod);
        if (psiCodeBlock == null) {
            return Lists.empty;
        }
        List<PsiStatement> result = new ArrayList<>();
        for (PsiElement child : psiCodeBlock.getChildren()) {
            if (child instanceof PsiStatement statement) {
                result.add(statement);
            }
        }
        return result;
    }


    public static PsiCodeBlock getPsiCodeBlock(PsiMethod psiMethod) {
        for (PsiElement child : psiMethod.getChildren()) {
            if (child instanceof PsiCodeBlock result) {
                return result;
            }
        }
        return null;
    }

    /**
     * 获取方法引用的类名和方法名
     *
     * @param methodReferenceExpression
     * @return
     */
    public static Pair<String, String> getMethodReferenceInfo(PsiMethodReferenceExpression methodReferenceExpression) {
        PsiMethod method = (PsiMethod) methodReferenceExpression.resolve();
        if (method == null) {
            return null;
        }
        PsiClass containingClass = method.getContainingClass();
        if (containingClass == null) {
            return null;
        }
        String className = method.getContainingClass().getQualifiedName();
        String methodName = methodReferenceExpression.getReferenceName();
        return new Pair<>(className, methodName);
    }

}
