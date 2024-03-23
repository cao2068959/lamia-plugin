package org.chy.lamiaplugin.expression;

import cn.hutool.core.lang.Pair;
import com.chy.lamia.convert.core.components.entity.Expression;
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
import org.chy.lamiaplugin.expression.entity.VarArgWrapper;

import java.util.List;
import java.util.function.Consumer;

public class LamiaExpressionResolver {

    public LamiaConvertInfo resolving(PsiMethodCallExpression methodCall) {
        LamiaConvertInfo result = new LamiaConvertInfo();
        // 解析表lamia达式
        Pair<LamiaExpression, PsiElement> lamiaExpressionAndLastSpi = parseMethod(methodCall, result);
        if (lamiaExpressionAndLastSpi == null) {
            return result;
        }
        result.setLamiaExpression(lamiaExpressionAndLastSpi.getKey());

        // 这里拿到的应该是最后一层的方法调用
        PsiElement lastMethodCall = lamiaExpressionAndLastSpi.getValue();
        // 如果设置了强转类型，那么去解析这个强转类型
        TypeDefinition targetType = getCastTarget(lastMethodCall);
        if (targetType != null) {
            result.setTargetType(targetType);
        }

        PsiLocalVariable localVariable = getLocalVariable(lastMethodCall);
        if (localVariable != null) {
            result.setResultVarName(localVariable.getName());
        }
        return result;
    }

    /**
     * 仅仅去解析整个lamia表达式
     *
     * @param methodCall        要解析的表达式
     * @param exceptionConsumer 如果异常如何处理
     * @return
     */
    public LamiaConvertInfo resolving(PsiMethodCallExpression methodCall, Consumer<Exception> exceptionConsumer) {
        try {
            return resolving(methodCall);
        } catch (Exception e) {
            exceptionConsumer.accept(e);
            return null;
        }
    }

    private TypeDefinition getCastTarget(PsiElement psiElement) {
        PsiTypeCastExpression castExpression = getCastExpression(psiElement);
        if (castExpression == null) {
            return null;
        }
        PsiTypeElement castType = castExpression.getCastType();
        if (castType == null) {
            return null;
        }
        String canonicalText = castType.getType().getCanonicalText();
        return new TypeDefinition(canonicalText);
    }


    /**
     * 解析整个 lamia表达式
     *
     * @return key把整个表达式解析之后的结果，value 解析结束之后最后一层的方法，用于后续继续继续从这个表达式上面拿数据
     */
    private Pair<LamiaExpression, PsiElement> parseMethod(PsiMethodCallExpression methodCall, LamiaConvertInfo convertInfo) {
        String name = MethodCallUtils.getMethodName(methodCall);
        PsiMethodWrapper psiMethodWrapper = new PsiMethodWrapper(methodCall);

        // 结束方法一共有 3个  convert / setArgs / build
        if ("mapping".equals(name)) {
            LamiaExpression result = new LamiaExpression();
            initPsiMethodWrapper(psiMethodWrapper, convertInfo);
            List<String> argsNames = psiMethodWrapper.useAllArgsToName();
            result.addSpreadArgs(argsNames, null);
            return new Pair<>(result, methodCall);
        }
        if ("setField".equals(name)) {
            LamiaExpression result = new LamiaExpression();
            initPsiMethodWrapper(psiMethodWrapper, convertInfo);
            List<String> argsNames = psiMethodWrapper.useAllArgsToName();
            result.addArgs(argsNames);
            return new Pair<>(result, methodCall);
        }

        if ("builder".equals(name)) {
            return parseBuildConfig(psiMethodWrapper, convertInfo);
        }

        return null;
    }

    /**
     * 如果表达式 使用了 lamia.build 的方法，那么去解析整个 build chain 的数据
     */
    private Pair<LamiaExpression, PsiElement> parseBuildConfig(PsiMethodWrapper psiMethodWrapper, LamiaConvertInfo convertInfo) {
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
            if (psiMethodCallExpression == null) {
                // 如果是最后一个 call 有可能会去设置最终要转换的类型 如 Lamia.builder()....build(arg) , 这里将会把这个 arg设置
                setTarget(convertInfo, methodCallWrapper);
                // 没有结束，但是已经无法继续往下解析了，说明表达式可能不完整
                if (!context.isEnd()) {
                    result.setParseComplete(false);
                }
                return new Pair<>(result, methodCallWrapper.getMethodCallExpression());
            }
            methodCallWrapper = new PsiMethodWrapper(psiMethodCallExpression);
            initPsiMethodWrapper(methodCallWrapper, convertInfo);
        }
    }


    private void setTarget(LamiaConvertInfo convertInfo, PsiMethodWrapper methodCallWrapper) {
        if (!"build".equals(methodCallWrapper.getName())) {
            return;
        }
        Expression expression = methodCallWrapper.useOnlyArgs();
        if (expression == null) {
            return;
        }
        String target = expression.get().toString();
        VarDefinition varDefinition = convertInfo.getArgs().get(target);
        convertInfo.setTarget(varDefinition);
        convertInfo.setTargetType(varDefinition.getType());
    }

    private void initPsiMethodWrapper(PsiMethodWrapper psiMethodWrapper, LamiaConvertInfo convertInfo) {
        psiMethodWrapper.initArgs(psiArgWrapper -> {
            if (psiArgWrapper instanceof VarArgWrapper varArgWrapper) {
                convertInfo.addVarArgs(new VarDefinition(varArgWrapper.getName(), new TypeDefinition(varArgWrapper.getVarType())));
            }
        });
    }


    private PsiMethodCallExpression nextMethodCall(PsiMethodCallExpression methodCall) {
        PsiElement psiElement = methodCall.getParent();
        while (true) {
            if (psiElement == null) {
                return null;
            }
            if (psiElement instanceof PsiLocalVariable || psiElement instanceof PsiCodeBlock ||
                    psiElement instanceof PsiStatement || psiElement instanceof PsiLambdaExpression) {
                return null;
            }
            if (psiElement instanceof PsiMethodCallExpression result) {
                return result;
            }
            psiElement = psiElement.getParent();
        }
    }

    private PsiTypeCastExpression getCastExpression(PsiElement data) {
        PsiElement psiElement = data;
        while (true) {
            if (psiElement == null) {
                return null;
            }
            if (psiElement instanceof PsiLocalVariable || psiElement instanceof PsiCodeBlock) {
                return null;
            }
            if (psiElement instanceof PsiTypeCastExpression result) {
                return result;
            }
            psiElement = psiElement.getParent();
        }
    }

    private PsiLocalVariable getLocalVariable(PsiElement data) {
        PsiElement psiElement = data;
        while (true) {
            if (psiElement == null) {
                return null;
            }
            if (psiElement instanceof PsiCodeBlock) {
                return null;
            }
            if (psiElement instanceof PsiLocalVariable result) {
                return result;
            }
            psiElement = psiElement.getParent();
        }
    }


}
