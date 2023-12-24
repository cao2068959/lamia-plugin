package org.chy.lamiaplugin.expression;

import com.chy.lamia.expose.Lamia;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiTreeChangeEventImpl;
import com.intellij.psi.impl.PsiTreeChangePreprocessor;
import com.intellij.psi.impl.file.impl.JavaFileManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.siyeh.ig.psiutils.VariableAccessUtils;
import org.chy.lamiaplugin.utlis.PsiTypeUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public class ConvertChangePreprocessor implements PsiTreeChangePreprocessor {

    private final LamiaExpressionManager lamiaExpressionManager;
    Project project;


    public ConvertChangePreprocessor(Project project) {
        this.project = project;
        this.lamiaExpressionManager = new LamiaExpressionManager(project);


        DumbService.getInstance(project).smartInvokeLater(() -> {
            PsiClass lamiaClass = JavaFileManager.getInstance(project).findClass(Lamia.class.getName(), GlobalSearchScope.allScope(project));
            if (lamiaClass == null) {
                return;
            }
            Collection<PsiReference> all = ReferencesSearch.search(lamiaClass).findAll();
            for (PsiReference psiReference : all) {
                addDependentType(psiReference);
            }
            System.out.println(all);
        });
    }

    private void addDependentType(PsiReference psiReference) {
        PsiElement completeExpression = getCompleteExpression(psiReference.getElement());
        if (completeExpression == null) {
            return;
        }
        PsiFile containingFile = psiReference.getElement().getContainingFile();
        if (containingFile == null) {
            return;
        }

        // 获取这个表达式中所有用到的变量
        Set<PsiVariable> psiVariables = VariableAccessUtils.collectUsedVariables(completeExpression);
        for (PsiVariable psiVariable : psiVariables) {
            PsiType type = psiVariable.getType();
            if (PsiTypeUtils.isUserDefinedClass(type)) {
                lamiaExpressionManager.addDependent(type.getCanonicalText(), containingFile);
            }
        }
        // 如果有强转类型，那么也依赖了强转
        if (completeExpression instanceof PsiTypeCastExpression castExpression) {
            PsiTypeElement castType = castExpression.getCastType();
            if (castType == null) {
                return;
            }
            PsiType type = castType.getType();
            if (PsiTypeUtils.isUserDefinedClass(type)) {
                lamiaExpressionManager.addDependent(type.getCanonicalText(), containingFile);
            }
        }
    }


    private PsiElement getCompleteExpression(PsiElement refExpr) {
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


    @Override
    public void treeChanged(@NotNull PsiTreeChangeEventImpl event) {
        if (event.getCode() != PsiTreeChangeEventImpl.PsiEventType.CHILDREN_CHANGED) {
            return;
        }
        // 发生变更之后执行


        System.out.println(event);
    }
}
