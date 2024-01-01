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
import org.apache.groovy.util.Maps;
import org.chy.lamiaplugin.components.MethodVariableCollector;
import org.chy.lamiaplugin.components.VariableCollector;
import org.chy.lamiaplugin.components.executor.LamiaExpressionChangeEvent;
import org.chy.lamiaplugin.components.executor.ScheduledBatchExecutor;
import org.chy.lamiaplugin.expression.entity.LamiaExpression;
import org.chy.lamiaplugin.expression.entity.RelationClassWrapper;
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

        // 获取到 这个lamia表达式的所有 依赖关系，key:依赖到的类的全路径，value: 这个类下面所有的字段
        Map<String, Set<String>> relations = LamiaExpressionManager.getInstance(project).getParticipateVar(lamiaStartExpression);

        LamiaExpression lamiaExpression = new LamiaExpression(lamiaStartExpression, containingFile);

        relations.forEach((classPath, fieldNames) -> {
            RelationClassWrapper relationClassWrapper = new RelationClassWrapper(classPath);
            relationClassWrapper.setFiledNames(fieldNames);
            relationClassWrapper.setLamiaExpression(lamiaExpression);
            lamiaExpressionManager.addRelations(relationClassWrapper);
        });

    }


    @Override
    public void treeChanged(@NotNull PsiTreeChangeEventImpl event) {
        if ( event.getCode() != PsiTreeChangeEventImpl.PsiEventType.CHILD_REPLACED) {
            return;
        }

        PsiElement newChild = event.getNewChild();

        if (event.getCode() == PsiTreeChangeEventImpl.PsiEventType.CHILD_REPLACED) {
            PsiMethodCallExpression methodCall = PsiMethodUtils.getMethodCall(newChild);
            if (methodCall == null){
                return;
            }
            PsiMethodCallExpression lamiaStartExpression = PsiMethodUtils.getLamiaStartExpression(methodCall);
            if (lamiaStartExpression == null){
                return;
            }
            LamiaExpressionChangeEvent scheduledEvent = new LamiaExpressionChangeEvent(lamiaStartExpression);
            ScheduledBatchExecutor.instance.deliverEvent(scheduledEvent);
            System.out.println(lamiaStartExpression);
        }

    }


}
