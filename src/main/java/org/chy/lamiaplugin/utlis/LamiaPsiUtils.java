package org.chy.lamiaplugin.utlis;

import com.chy.lamia.convert.core.entity.BuildInfo;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.chy.lamiaplugin.exception.LamiaException;

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
            PsiStatement statement = PsiMethodUtils.getBelongPsiStatement(psiElement);
            if (statement == null) {
                return;
            }
            PsiElement belongPsiCodeBlockElement = statement;
            // 说明目标语句要放到 return 语句之后，这里把 return 拆开
            if (statement instanceof PsiReturnStatement returnStatement) {
                belongPsiCodeBlockElement = splitReturnStatement(returnStatement, psiElementFactory);
            }

            PsiElement element = belongPsiCodeBlockElement.getParent()
                    .addAfter(newElement, belongPsiCodeBlockElement);
            result.setData(element);
        });

        return result.data;
    }

    /**
     * 拆分 return 表达式
     *
     * @param returnStatement
     * @return
     */
    private static PsiElement splitReturnStatement(PsiReturnStatement returnStatement, PsiElementFactory psiElementFactory) {
        // 获取最近的一层执行表达式
        PsiElement recentlyExecExpression = PsiMethodUtils.getRecentlyExecExpression(returnStatement, 3);
        PsiType type = PsiTypeUtils.getType(recentlyExecExpression);
        if (type == null) {
            throw new LamiaException("return 语句 :" + returnStatement + " 无法找到要返回的类型是什么");
        }
        // 生成新的变量引用语句
        PsiElement varRefExpression = psiElementFactory.createStatementFromText(type.getPresentableText() + " result =  " + recentlyExecExpression.getText(), null);
        varRefExpression = returnStatement.replace(varRefExpression);

        // 生成对应的 return 语句
        PsiElement returnExpression = psiElementFactory.createStatementFromText("return result;", null);
        varRefExpression.getParent().addAfter(returnExpression, varRefExpression);

        return varRefExpression;
    }

}
