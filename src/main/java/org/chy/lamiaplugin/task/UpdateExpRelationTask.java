package org.chy.lamiaplugin.task;

import com.chy.lamia.expose.Lamia;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.impl.JavaFileManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.chy.lamiaplugin.expression.LamiaExpressionManager;
import org.chy.lamiaplugin.utlis.PsiMethodUtils;

import java.util.Collection;

public class UpdateExpRelationTask extends Task.Backgroundable {


    private final boolean isNotify;

    public UpdateExpRelationTask(Project project, boolean isNotify) {
        super(project, "lamia expression relation update", true);
        this.isNotify = isNotify;

    }

    @Override
    public void run(ProgressIndicator indicator) {

        try {
            ApplicationManager.getApplication().runReadAction(() -> doRun(indicator));
        } catch (Exception e) {
            showNotification("lamia expression relation update fail, msg: [" + e.getMessage() + "]", NotificationType.ERROR);
        }
    }


    public void doRun(ProgressIndicator indicator) {

        Project project = getProject();
        LamiaExpressionManager manager = LamiaExpressionManager.getInstance(getProject());

        PsiClass lamiaClass = JavaFileManager.getInstance(project).findClass(Lamia.class.getName(), GlobalSearchScope.allScope(project));
        indicator.setFraction(0.3);
        if (lamiaClass == null) {
            showNotification("lamia class not found", NotificationType.INFORMATION);
            return;
        }
        Collection<PsiReference> all = ReferencesSearch.search(lamiaClass).findAll();
        if (all.isEmpty()) {
            showNotification("No lamia expression found", NotificationType.INFORMATION);
            return;
        }

        indicator.setFraction(0.5);

        int count = all.size();
        int index = 0;
        for (PsiReference psiReference : all) {
            addRelation(psiReference, manager);

            double progressed = progressCalculate(0.5, count, ++index);
            indicator.setFraction(progressed);
        }

        showNotification("lamia expression relation update success", NotificationType.INFORMATION);
    }


    private double progressCalculate(double current, int count, int index) {
        double remaining = 1 - current;
        return remaining * index / count;
    }


    /**
     * 收集
     *
     * @param psiReference
     */
    private void addRelation(PsiReference psiReference, LamiaExpressionManager manager) {
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
        manager.updateDependentRelations(lamiaStartExpression);
    }

    public void showNotification(String content, NotificationType type) {
        if (type != NotificationType.ERROR && !isNotify) {
            return;
        }

        Notification notification = new Notification(
                "UpdateExpRelationTaskNotification",
                "lamia",
                content,
                type
        );
        Notifications.Bus.notify(notification);
    }
}