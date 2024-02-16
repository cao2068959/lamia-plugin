package org.chy.lamiaplugin.expression;

import com.chy.lamia.expose.Lamia;
import com.chy.lamia.utils.Lists;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiTreeChangeEventImpl;
import com.intellij.psi.impl.PsiTreeChangePreprocessor;
import com.intellij.psi.impl.file.impl.JavaFileManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.chy.lamiaplugin.components.executor.ChangeType;
import org.chy.lamiaplugin.components.executor.LamiaExpressionChangeEvent;
import org.chy.lamiaplugin.components.executor.ScheduledBatchExecutor;
import org.chy.lamiaplugin.utlis.PsiMethodUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.intellij.psi.impl.PsiTreeChangeEventImpl.PsiEventType.*;

public class ConvertChangePreprocessor implements PsiTreeChangePreprocessor {

    private final LamiaExpressionManager lamiaExpressionManager;
    Project project;


    public ConvertChangePreprocessor(Project project) {
        this.project = project;
        this.lamiaExpressionManager = LamiaExpressionManager.getInstance(project);

        DumbService.getInstance(project).smartInvokeLater(() -> {
            PsiClass lamiaClass = JavaFileManager.getInstance(project).findClass(Lamia.class.getName(), GlobalSearchScope.allScope(project));
            if (lamiaClass == null) {
                return;
            }
            Collection<PsiReference> all = ReferencesSearch.search(lamiaClass).findAll();
            for (PsiReference psiReference : all) {
                addRelation(psiReference);
            }
            System.out.println(all);
        });
    }

    /**
     * 收集
     *
     * @param psiReference
     */
    private void addRelation(PsiReference psiReference) {
        PsiElement element = psiReference.getElement();
        PsiFile containingFile = element.getContainingFile();
        if (containingFile == null) {
            return;
        }
        PsiMethodCallExpression methodCall = PsiMethodUtils.getMethodCall(element);
        if (methodCall == null) {
            return;
        }
        PsiMethodCallExpression lamiaStartExpression = PsiMethodUtils.getLamiaStartExpression(methodCall);
        if (lamiaStartExpression == null) {
            return;
        }

        // 将这个表达式添加进 manager 中，生成包括依赖关系的索引
        lamiaExpressionManager.updateDependentRelations(lamiaStartExpression);
    }


    @Override
    public void treeChanged(@NotNull PsiTreeChangeEventImpl event) {
        if (!(event.getCode() == CHILD_REPLACED ||
                event.getCode() == BEFORE_CHILD_REPLACEMENT ||
                event.getCode() == CHILD_REMOVED || event.getCode() == CHILD_ADDED)) {
            return;
        }
        if (event.getCode() == CHILD_REMOVED) {
            PsiMethodCallExpression lamiaStartExpression = getLamiaExpression(event.getChild(), false);
            if (lamiaStartExpression == null) {
                return;
            }
            ScheduledBatchExecutor.instance.deliverEvent(new LamiaExpressionChangeEvent(lamiaStartExpression, ChangeType.delete, project));
            System.out.println("tree删除了 --->" + lamiaStartExpression);
            return;
        }

        if (event.getCode() == BEFORE_CHILD_REPLACEMENT) {
            beforeChildReplacementHandler(event);
            return;
        }

        if (event.getCode() == CHILD_REPLACED) {
            PsiMethodCallExpression lamiaStartExpression = getLamiaExpression(event.getNewChild(), false);
            if (lamiaStartExpression == null) {
                return;
            }
            ScheduledBatchExecutor.instance.deliverEvent(new LamiaExpressionChangeEvent(lamiaStartExpression, ChangeType.update, project));
            System.out.println("tree改变了 --->" + lamiaStartExpression);
            return;
        }

        if (event.getCode() == CHILD_ADDED) {
            extractExpressionFromMethod(event.getChild()).forEach(lamiaStartExpression -> {
                ScheduledBatchExecutor.instance.deliverEvent(new LamiaExpressionChangeEvent(lamiaStartExpression, ChangeType.update, project));
                System.out.println("tree添加了 --->" + lamiaStartExpression);
            });

        }

    }


    private void beforeChildReplacementHandler(PsiTreeChangeEventImpl event) {
        PsiElement oldChild = event.getOldChild();
        if (oldChild == null) {
            return;
        }
        String text = oldChild.getText();
        // 修改了标志位
        if ("Lamia".equals(text)) {
            PsiElement newChild = event.getNewChild();
            // Lamia的标志位没有变动不需要修改
            if (newChild != null && "Lamia".equals(newChild.getText())) {
                return;
            }
            PsiMethodCallExpression lamiaStartExpression = getLamiaExpression(event.getOldChild(), false);
            if (lamiaStartExpression == null) {
                return;
            }
            ScheduledBatchExecutor.instance.deliverEvent(new LamiaExpressionChangeEvent(lamiaStartExpression, ChangeType.delete, project));
            return;
        }

        PsiMethodCallExpression lamiaStartExpression = getLamiaExpression(event.getNewChild(), true);
        if (lamiaStartExpression == null) {
            return;
        }
        ScheduledBatchExecutor.instance.deliverEvent(new LamiaExpressionChangeEvent(lamiaStartExpression, ChangeType.update, project));
    }


    private List<PsiMethodCallExpression> extractExpressionFromMethod(PsiElement psiElement) {
        // 如果不是方法，那么就直接
        if (!(psiElement instanceof PsiMethod psiMethod)) {
            PsiMethodCallExpression lamiaExpression = getLamiaExpression(psiElement, false);
            if (lamiaExpression != null) {
                return List.of(lamiaExpression);
            }
            return Lists.empty;
        }
        List<PsiStatement> psiStatement = PsiMethodUtils.getPsiStatement(psiMethod);
        List<PsiMethodCallExpression> result = new ArrayList<>();
        for (PsiStatement statement : psiStatement) {
            PsiMethodCallExpression lamiaExpression = getLamiaExpression(statement, false);
            if (lamiaExpression != null) {
                result.add(lamiaExpression);
            }
        }
        return result;
    }

    /**
     * 从 statement 中获取 lamia 表达式
     */
    private PsiMethodCallExpression getLamiaExpression(PsiElement psiElement, boolean reCheck) {
        PsiMethodCallExpression methodCall;
        if (psiElement instanceof PsiStatement) {
            methodCall = PsiMethodUtils.getMethodCallExpressionFromChildren(psiElement, 3);
        } else {
            methodCall = PsiMethodUtils.getMethodCall(psiElement);
            if (reCheck && methodCall == null) {
                methodCall = PsiMethodUtils.getMethodCallExpressionFromChildren(psiElement, 3);
            }
        }
        if (methodCall == null) {
            return null;
        }
        return PsiMethodUtils.getLamiaStartExpression(methodCall);
    }


}
