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
import org.chy.lamiaplugin.components.MethodVariableCollector;
import org.chy.lamiaplugin.components.VariableCollector;
import org.chy.lamiaplugin.utlis.PsiMethodUtils;
import org.chy.lamiaplugin.utlis.PsiTypeUtils;
import org.chy.lamiaplugin.utlis.Sets;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
        PsiElement completeExpression = PsiMethodUtils.getCompleteExpression(psiReference.getElement());
        if (completeExpression == null) {
            return;
        }
        PsiFile containingFile = psiReference.getElement().getContainingFile();
        if (containingFile == null) {
            return;
        }
        // 获取这个表达式中所有用到的变量
        Set<String> dependentType = getDependentType(completeExpression);
        for (String type : dependentType) {
            lamiaExpressionManager.addDependent(type, containingFile);
        }

    }

    Map<PsiElement, Set<String>> dependentTempCache = new ConcurrentHashMap<>();

    @Override
    public void treeChanged(@NotNull PsiTreeChangeEventImpl event) {
        if (!(event.getCode() == PsiTreeChangeEventImpl.PsiEventType.BEFORE_CHILD_REPLACEMENT
                || event.getCode() == PsiTreeChangeEventImpl.PsiEventType.CHILD_REPLACED)) {
            return;
        }

        PsiElement oldChild = event.getOldChild();
        PsiElement newChild = event.getNewChild();

        if (event.getCode() == PsiTreeChangeEventImpl.PsiEventType.BEFORE_CHILD_REPLACEMENT) {
            if (oldChild != null) {
                // 获取在修改表达式之前的依赖
                PsiElement newExpression = PsiMethodUtils.getCompleteExpression(oldChild);
                Set<String> oldDependentType = getLamiaDependentType(newExpression);
                dependentTempCache.put(oldChild, oldDependentType);
            }
            return;
        }


        if (event.getCode() == PsiTreeChangeEventImpl.PsiEventType.CHILD_REPLACED) {
            Set<String> dependentType = null;
            if (newChild != null) {
                // 获取在修改表达式之前的依赖
                PsiElement newExpression = PsiMethodUtils.getCompleteExpression(newChild);
                dependentType = getLamiaDependentType(newExpression);

                PsiMethod belongMethod = PsiMethodUtils.getBelongMethod(newExpression);
                MethodVariableCollector collector = new MethodVariableCollector();
                belongMethod.accept(collector);
                Set<PsiVariable> variables = collector.getVariables();
                System.out.println(variables);
            }
            try {
                Set<String> oldDependent = dependentTempCache.get(oldChild);
                Sets.compare(dependentType, oldDependent, addType -> {
                    System.out.println("添加了" + addType);
                }, deleteType -> {
                    System.out.println("删除了" + deleteType);
                });

            } finally {
                dependentTempCache.remove(oldChild);
            }

            return;
        }

    }

    private Set<String> getLamiaDependentType(PsiElement expression) {
        if (expression == null) {
            return Sets.emptySet();
        }
        if (!PsiMethodUtils.isLamiaExpression(expression)) {
            return Sets.emptySet();
        }
        return getDependentType(expression);
    }

    private Set<String> getDependentType(PsiElement expression) {
        Set<String> result = new HashSet<>();
        // 获取这个表达式中所有用到的变量
        Set<PsiVariable> psiVariables = VariableAccessUtils.collectUsedVariables(expression);
        for (PsiVariable psiVariable : psiVariables) {
            PsiType type = psiVariable.getType();
            if (PsiTypeUtils.isUserDefinedClass(type)) {
                result.add(type.getCanonicalText());
            }
        }
        // 如果有强转类型，那么也依赖了强转
        if (expression instanceof PsiTypeCastExpression castExpression) {
            PsiTypeElement castType = castExpression.getCastType();
            if (castType == null) {
                return result;
            }
            PsiType type = castType.getType();
            if (PsiTypeUtils.isUserDefinedClass(type)) {
                result.add(type.getCanonicalText());
            }
        }
        return result;
    }

}
