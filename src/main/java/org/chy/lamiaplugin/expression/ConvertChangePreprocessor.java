package org.chy.lamiaplugin.expression;

import com.chy.lamia.convert.core.components.ComponentFactory;
import com.chy.lamia.convert.core.components.NameHandler;
import com.chy.lamia.convert.core.components.TreeFactory;
import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.Statement;
import com.chy.lamia.utils.Lists;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiTreeChangeEventImpl;
import com.intellij.psi.impl.PsiTreeChangePreprocessor;

import org.chy.lamiaplugin.components.executor.*;
import org.chy.lamiaplugin.expression.components.SimpleNameHandler;
import org.chy.lamiaplugin.expression.components.StringExpression;
import org.chy.lamiaplugin.expression.components.StringTreeFactory;
import org.chy.lamiaplugin.expression.components.statement.StringStatement;
import org.chy.lamiaplugin.utlis.PsiMethodUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.AnnotatedElement;
import java.util.*;

import static com.intellij.psi.impl.PsiTreeChangeEventImpl.PsiEventType.*;

public class ConvertChangePreprocessor implements PsiTreeChangePreprocessor {

    private final LamiaExpressionManager lamiaExpressionManager;
    Project project;

    static boolean applicationLoaded = false;


    public ConvertChangePreprocessor(Project project) {
        applicationLoad();
        projectLoad(project);
        this.project = project;
        this.lamiaExpressionManager = LamiaExpressionManager.getInstance(project);
    }


    public void projectLoad(Project project) {
        ScheduledBatchExecutor.instance.registerBatchExecutor(new LamiaExpressionChangeExecutor(project));
    }

    public void applicationLoad() {
        if (applicationLoaded) {
            return;
        }
        synchronized (ConvertChangePreprocessor.class) {
            if (applicationLoaded) {
                return;
            }
            doApplicationLoad();
            applicationLoaded = true;
        }
    }

    public void doApplicationLoad() {
        ScheduledBatchExecutor.instance = new ScheduledBatchExecutor(6000);
        ScheduledBatchExecutor.instance.registerBatchExecutor(new BuildRefreshExecutor());
        ScheduledBatchExecutor.instance.registerBatchExecutor(new UpdateExpRelationExecutor());
        registerLamiaComponents();
    }

    private void registerLamiaComponents() {
        ComponentFactory.registerComponents(TreeFactory.class, new StringTreeFactory());
        ComponentFactory.registerEntityStructure(Expression.class, StringExpression::new);
        ComponentFactory.registerEntityStructure(Statement.class, StringStatement::new);
        ComponentFactory.registerComponents(NameHandler.class, new SimpleNameHandler());
    }


    @Override
    public void treeChanged(@NotNull PsiTreeChangeEventImpl event) {
        if (!(event.getCode() == CHILD_REPLACED ||
                event.getCode() == BEFORE_CHILD_REPLACEMENT ||
                event.getCode() == CHILD_REMOVED || event.getCode() == CHILD_ADDED)) {
            return;
        }

        buildRefresh(event);

        if (event.getCode() == CHILD_REMOVED) {
            PsiMethodCallExpression lamiaStartExpression = getLamiaExpression(event.getChild(), false);
            if (lamiaStartExpression == null) {
                return;
            }
            ScheduledBatchExecutor.instance.deliverEvent(new LamiaExpressionChangeEvent(lamiaStartExpression, ChangeType.delete, project));
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
            return;
        }

        if (event.getCode() == CHILD_ADDED) {
            extractExpressionFromMethod(event.getChild()).forEach(lamiaStartExpression -> {
                ScheduledBatchExecutor.instance.deliverEvent(new LamiaExpressionChangeEvent(lamiaStartExpression, ChangeType.update, project));
            });
        }
    }


    private void buildRefresh(PsiTreeChangeEventImpl event) {
        PsiElement child = event.getChild();
        if (child == null) {
            child = event.getOldChild();
        }

        // 这个元素不需要刷新
        if (!isBuildRefreshElement(child, true)) {
            return;
        }

        ScheduledBatchExecutor.instance.deliverEvent(new FileChangeEvent(event.getFile()));
    }

    private boolean isBuildRefreshElement(PsiElement psiElement, boolean continueCheck) {
        if (psiElement == null) {
            return false;
        }

        if (psiElement instanceof PsiField) {
            return true;
        }

        if (psiElement instanceof PsiMethod) {
            return true;
        }

        if (psiElement instanceof AnnotatedElement || psiElement instanceof PsiAnnotation) {
            return true;
        }

        if (psiElement instanceof PsiJavaCodeReferenceElement && psiElement.getParent() instanceof PsiAnnotation) {
            return true;
        }


        if (continueCheck) {
            // 他的父类是否符合类型条件
            return isBuildRefreshElement(psiElement.getParent(), false);
        }
        return false;
    }


    private void beforeChildReplacementHandler(PsiTreeChangeEventImpl event) {
        PsiElement oldChild = event.getOldChild();
        if (oldChild != null) {
            String text = oldChild.getText();
            // 修改了标志位
            if ("Lamia".equals(text)) {
                updateLamiaFlag(event);
            }
        }
        PsiElement child = event.getNewChild();
        if (isInvalidElement(child)) {
            PsiMethodCallExpression lamiaStartExpression = getLamiaExpression(event.getNewChild(), false);
            if (lamiaStartExpression == null) {
                return;
            }
            ScheduledBatchExecutor.instance.deliverEvent(new LamiaExpressionChangeEvent(lamiaStartExpression, ChangeType.update, project));
        }
    }

    private boolean isInvalidElement(PsiElement psiElement) {
        if (psiElement == null) {
            return false;
        }
        if (psiElement instanceof PsiWhiteSpace) {
            return false;
        }
        return true;
    }

    private void updateLamiaFlag(PsiTreeChangeEventImpl event) {
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
