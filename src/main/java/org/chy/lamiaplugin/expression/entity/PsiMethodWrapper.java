package org.chy.lamiaplugin.expression.entity;

import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.chy.lamia.convert.core.expression.parse.entity.ArgWrapper;
import com.chy.lamia.convert.core.expression.parse.entity.MethodWrapper;
import com.chy.lamia.convert.core.utils.struct.Pair;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiMethodUtil;
import com.siyeh.ig.psiutils.MethodCallUtils;
import com.siyeh.ig.psiutils.VariableAccessUtils;
import org.chy.lamiaplugin.expression.components.MethodCallParamStringExpression;
import org.chy.lamiaplugin.expression.components.MethodReferenceStringExpression;
import org.chy.lamiaplugin.expression.components.ParamStringExpression;
import org.chy.lamiaplugin.expression.components.StringExpression;
import org.chy.lamiaplugin.utlis.PsiMethodUtils;
import org.chy.lamiaplugin.utlis.PsiTypeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PsiMethodWrapper extends MethodWrapper {

    private final PsiMethodCallExpression methodCallExpression;

    public PsiMethodWrapper(PsiMethodCallExpression methodCallExpression) {
        super(MethodCallUtils.getMethodName(methodCallExpression));
        this.methodCallExpression = methodCallExpression;
    }

    public void initArgs(Consumer<PsiArgWrapper> useFun) {
        List<ArgWrapper> arg = new ArrayList<>();
        setArgs(arg);
        PsiExpression[] expressions = methodCallExpression.getArgumentList().getExpressions();
        for (PsiExpression expression : expressions) {
            PsiArgWrapper wrapper = genArgWrapper(expression);
            if (wrapper != null) {
                wrapper.setUseFun(useFun);
                arg.add(wrapper);
            }
        }
    }

    private PsiArgWrapper genArgWrapper(PsiExpression expression) {
        if (expression instanceof PsiMethodReferenceExpression methodReferenceExpression) {
            Pair<String, String> methodReferenceInfo = PsiMethodUtils.getMethodReferenceInfo(methodReferenceExpression);
            if (methodReferenceInfo == null) {
                return null;
            }
            return new MethodReferenceArgWrapper(new MethodReferenceStringExpression(methodReferenceInfo),
                    methodReferenceInfo.getRight());
        }

        if (expression instanceof PsiReferenceExpression referenceExpression) {
            PsiType type = referenceExpression.getType();
            if (type == null) {
                return null;
            }
            String varName = referenceExpression.getText();
            VarArgWrapper result = new VarArgWrapper(new ParamStringExpression(varName), varName);
            result.setVarType(PsiTypeUtils.toTypeDefinition(type));
            return result;
        }
        if (expression instanceof PsiClassObjectAccessExpression accessExpression) {
            String type = accessExpression.getOperand().getType().getCanonicalText();
            return new ClassAccessArgWrapper(new StringExpression(type), type);
        }

        if (expression instanceof PsiMethodCallExpression methodCallExpression) {
            // 这个表达式执行之后的类型
            PsiType execType = methodCallExpression.getType();
            if (execType == null) {
                return null;
            }

            TypeDefinition typeDefinition = PsiTypeUtils.toTypeDefinition(execType);
            MethodCallParamStringExpression stringExpression = new MethodCallParamStringExpression(typeDefinition, methodCallExpression.getText());
            VarArgWrapper result = new VarArgWrapper(stringExpression, null);
            result.setMethodInvoke(true);
            result.setVarType(typeDefinition);
            return result;
        }

        if (expression instanceof PsiThisExpression thisExpression) {
            PsiType type = thisExpression.getType();
            if (type == null) {
                return null;
            }
            VarArgWrapper result = new VarArgWrapper(new ParamStringExpression("this"), "this");
            result.setVarType(PsiTypeUtils.toTypeDefinition(type));
            return result;
        }

        return null;
    }

    public PsiMethodCallExpression getMethodCallExpression() {
        return methodCallExpression;
    }
}
