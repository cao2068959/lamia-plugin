package org.chy.lamiaplugin.expression;

import com.chy.lamia.convert.core.entity.LamiaConvertInfo;
import com.chy.lamia.convert.core.entity.LamiaExpression;
import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.chy.lamia.convert.core.entity.VarDefinition;
import com.chy.lamia.convert.core.expression.parse.ConfigParseContext;
import com.chy.lamia.convert.core.expression.parse.builder.BuilderContext;
import com.chy.lamia.convert.core.expression.parse.builder.BuilderHandler;
import com.intellij.psi.*;
import com.siyeh.ig.psiutils.MethodCallUtils;
import org.chy.lamiaplugin.expression.entity.PsiMethodWrapper;

import java.util.List;

public class LamiaExpressionResolver {

    public LamiaConvertInfo resolving(PsiElement psiElement) {
        LamiaConvertInfo result = new LamiaConvertInfo();
        PsiMethodCallExpression methodCall = (PsiMethodCallExpression) psiElement;

        LamiaExpression lamiaExpression = parseMethod(methodCall, result);
        result.setLamiaExpression(lamiaExpression);
        return result;

    }

    private LamiaExpression parseMethod(PsiMethodCallExpression methodCall, LamiaConvertInfo convertInfo) {
        String name = MethodCallUtils.getMethodName(methodCall);
        PsiMethodWrapper psiMethodWrapper = new PsiMethodWrapper(methodCall);

        // 结束方法一共有 3个  convert / setArgs / build
        if ("mapping".equals(name)) {
            LamiaExpression result = new LamiaExpression();
            initPsiMethodWrapper(psiMethodWrapper, convertInfo);
            List<String> argsNames = psiMethodWrapper.useAllArgsToName();
            result.addSpreadArgs(argsNames, null);
            return result;
        }
        if ("setField".equals(name)) {
            LamiaExpression result = new LamiaExpression();
            initPsiMethodWrapper(psiMethodWrapper, convertInfo);
            List<String> argsNames = psiMethodWrapper.useAllArgsToName();
            result.addArgs(argsNames);
            return result;
        }

        if ("builder".equals(name)) {
            return parseBuildConfig(psiMethodWrapper, convertInfo);
        }

        return null;
    }

    private LamiaExpression parseBuildConfig(PsiMethodWrapper psiMethodWrapper, LamiaConvertInfo convertInfo) {
        LamiaExpression result = new LamiaExpression();
        // 创建配置解析器 准备开始解析
        ConfigParseContext context = new ConfigParseContext();

        PsiMethodWrapper methodCallWrapper = psiMethodWrapper;

        while (true) {
            String name = methodCallWrapper.getName();
            String key = context.getScope(name);
            BuilderHandler handler = BuilderContext.getHandler(key);
            if (handler == null) {
                throw new RuntimeException("[LamiaExpressionResolver] 无法找到 配置处理器 key: [" + key + "]");
            }

            // 执行handler策略
            handler.config(result, methodCallWrapper, context);

            // 获取链式调用的下一个方法
            PsiMethodCallExpression psiMethodCallExpression = nextMethodCall(methodCallWrapper.getMethodCallExpression());
            if (psiMethodCallExpression == null){
                return result;
            }
            methodCallWrapper = new PsiMethodWrapper(psiMethodCallExpression);
            initPsiMethodWrapper(methodCallWrapper, convertInfo);
        }
    }

    private void initPsiMethodWrapper(PsiMethodWrapper psiMethodWrapper, LamiaConvertInfo convertInfo) {
        psiMethodWrapper.initArgs(psiArgWrapper -> {
            PsiVariable psiVariable = psiArgWrapper.getPsiVariable();
            PsiType type = psiVariable.getType();
            convertInfo.addVarArgs(new VarDefinition(psiVariable.getName(), new TypeDefinition(type.getCanonicalText())));
        });

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
