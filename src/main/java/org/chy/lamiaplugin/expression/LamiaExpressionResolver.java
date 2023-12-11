package org.chy.lamiaplugin.expression;

import com.chy.lamia.convert.core.entity.LamiaConvertInfo;
import com.chy.lamia.convert.core.entity.LamiaExpression;
import com.chy.lamia.convert.core.expression.parse.entity.MethodWrapper;
import com.intellij.psi.*;
import com.siyeh.ig.psiutils.MethodCallUtils;
import com.siyeh.ig.psiutils.VariableAccessUtils;
import com.sun.tools.javac.tree.JCTree;
import org.chy.lamiaplugin.expression.entity.PsiMethodWrapper;

import java.util.List;
import java.util.Set;

public class LamiaExpressionResolver {

    public LamiaConvertInfo resolving(PsiElement psiElement) {
        LamiaConvertInfo result = new LamiaConvertInfo();
        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) psiElement;


        return null;

    }

    private LamiaExpression parseMethod(PsiMethodCallExpression methodCall) {
        String name = MethodCallUtils.getMethodName(methodCall);

        PsiMethodWrapper psiMethodWrapper = new PsiMethodWrapper(name, methodCall);



        // 结束方法一共有 3个  convert / setArgs / build
        if ("mapping".equals(name)) {
            LamiaExpression result = new LamiaExpression();
            List<String> argsNames = fetchArgsName(data);
            result.addSpreadArgs(argsNames, null);
            return result;
        }
        if ("setField".equals(name)) {
            LamiaExpression result = new LamiaExpression();
            List<String> argsNames = fetchArgsName(data);
            result.addArgs(argsNames);
            return result;
        }

        if ("build".equals(name)) {
            LamiaExpression result = new LamiaExpression();
            parseBuildConfig(result, data);
            return result;
        }

        return null;
    }


    private PsiMethodCallExpression nextMethodCall(PsiMethodCallExpression methodCall) {
        PsiElement psiElement = methodCall.getParent();
        while (true) {
            if (psiElement == null) {
                return null;
            }
            if (psiElement instanceof PsiLocalVariable || psiElement instanceof PsiCodeBlock ||
                    psiElement instanceof PsiStatement) {
                return null;
            }
            if (psiElement instanceof PsiMethodCallExpression result) {
                return result;
            }
            psiElement = psiElement.getParent();
        }

    }


}
