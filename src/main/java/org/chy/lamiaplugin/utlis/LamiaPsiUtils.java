package org.chy.lamiaplugin.utlis;

import com.chy.lamia.convert.core.entity.BuildInfo;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethodCallExpression;
import org.chy.lamiaplugin.exception.LamiaException;

import java.lang.invoke.LambdaConversionException;

public class LamiaPsiUtils {

    /**
     * 插入 指定的规则
     *
     * @param buildInfo
     * @param ruleText
     */
    public static void insertRule(BuildInfo buildInfo, String ruleText, Project project) {
        Object holder = buildInfo.getHolder();
        if (!(holder instanceof PsiMethodCallExpression callExpression)) {
            return;
        }
        StringBuilder insertText = new StringBuilder();
        PsiElement replaceExpression = null;


        // 如果不是 builder模式，那么需要先开启 builder模式
        if (buildInfo.isBuilder()) {
            replaceExpression = PsiMethodUtils.getMethodCallExpressionFromChildren(callExpression, false, 5);
            if (replaceExpression == null) {
                throw new LamiaException("无法找到 表达式[" + callExpression.getText() + "] 的上一层表达式");
            }
            String text = replaceExpression.getText();
            insertText.append(text);
        } else {
            replaceExpression = PsiMethodUtils.getLamiaStartReference(callExpression);
            if (replaceExpression == null) {
                throw new LamiaException("无法找到 表达式[" + callExpression.getText() + "] 的Lamia 标识符");
            }
            insertText.append("Lamia.builder()");
        }

        // 没开rule模式，帮他开启对应的rule模式
        if (buildInfo.getRuleInfo() == null) {
            insertText.append(".rule()");
        }

        insertText.append(ruleText);

        PsiElementFactory psiElementFactory = PsiElementFactory.getInstance(project);

        PsiElement finalReplaceExpression = replaceExpression;
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiElement newCall = psiElementFactory.createExpressionFromText(insertText.toString(), null);
            finalReplaceExpression.replace(newCall);

            // 如果不是 builder模式，还要在结尾加上 build()
            if (!buildInfo.isBuilder()) {
                String newCode = callExpression.getText() + ".build()";
                newCall = psiElementFactory.createExpressionFromText(newCode, null);
                callExpression.replace(newCall);
            }

        });
    }


    public static PsiElement insertCodeAfter(PsiElement psiElement, String newCode, Project project) {

        PsiElementFactory psiElementFactory = PsiElementFactory.getInstance(project);
        PsiElement newElement = psiElementFactory.createStatementFromText(newCode, null);

        Wrapper<PsiElement> result = new Wrapper<>();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiElement belongPsiCodeBlockElement = PsiMethodUtils.getBelongPsiCodeBlockElement(psiElement);
            if (belongPsiCodeBlockElement == null) {
                return;
            }
            PsiElement element = belongPsiCodeBlockElement.getParent()
                    .addAfter(newElement, belongPsiCodeBlockElement);

            result.setData(element);
        });

        return result.data;
    }

}
