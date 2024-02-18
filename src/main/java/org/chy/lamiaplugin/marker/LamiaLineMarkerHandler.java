package org.chy.lamiaplugin.marker;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.*;
import com.intellij.ui.awt.RelativePoint;
import org.chy.lamiaplugin.expression.ConvertResult;
import org.chy.lamiaplugin.expression.LamiaExpressionManager;
import org.chy.lamiaplugin.expression.entity.LamiaExpression;
import org.chy.lamiaplugin.expression.entity.RelationClassWrapper;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bignosecat
 */
public class LamiaLineMarkerHandler {

    private static Map<Project, LamiaLineMarkerHandler> instanceList = new ConcurrentHashMap<>();

    private final MarkerMessagePanel markerMessagePanel;

    public static LamiaLineMarkerHandler of(Project project) {
        return instanceList.computeIfAbsent(project, LamiaLineMarkerHandler::new);
    }

    public LamiaLineMarkerHandler(Project project) {
        this.markerMessagePanel = new MarkerMessagePanel(project);
    }

    public void click(MouseEvent event, PsiMethodCallExpression lamiaMethod, boolean isComplete) {
        showTip("出现----->", event, lamiaMethod, isComplete);
    }


    private void showTip(String msg, MouseEvent event, PsiMethodCallExpression psiElement, boolean isComplete) {

        Project project = psiElement.getProject();

        // 用Lamia表达式生成对应的代码
        LamiaCode lamiaCode = getLamaCode(psiElement, project, isComplete);

        // 设置要显示的数据
        setPanelShowData(lamiaCode, project, psiElement);

        Insets customInsets = new Insets(0, 0, 0, 0);
        ApplicationManager.getApplication().invokeLater(() -> {
            JBPopupFactory.getInstance().createBalloonBuilder(markerMessagePanel)
                    .setAnimationCycle(10)
                    .setBorderInsets(customInsets)
                    .setBorderColor(Color.GRAY)
                    .setShadow(true)
                    .setHideOnAction(false)
                    .createBalloon().show(new RelativePoint(event), Balloon.Position.below);
        });
        //.show(new RelativePoint(event), Balloon.Position.below));
    }

    private void setPanelShowData(LamiaCode lamiaCode, Project project, PsiMethodCallExpression psiElement) {
        if (!lamiaCode.success) {
            markerMessagePanel.fail("Conversion failed", lamiaCode.data);
            return;
        }
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        PsiElement parentMethod = getSpiCodeBlock(psiElement);

        // 把这个转换语句放入代码块中用于显示
        JavaCodeFragmentFactory fragmentFactory = JavaCodeFragmentFactory.getInstance(project);
        JavaCodeFragment code = fragmentFactory.createCodeBlockCodeFragment(lamiaCode.getData(), parentMethod, true);

        // 有一个 外部 import的 把他放入到 JavaCodeFragment
        Set<String> importClassPath = lamiaCode.importClassPath;
        if (importClassPath != null && !importClassPath.isEmpty()) {
            for (String importClass : importClassPath) {
                code.addImportsFromString(importClass);
            }
        }
        Document document = documentManager.getDocument(code);
        markerMessagePanel.success(document);

        // 去检查这个 表达式是否已经关联上了
        LamiaExpressionManager manager = LamiaExpressionManager.getInstance(project);
        LamiaExpression lamiaExpression = new LamiaExpression(psiElement);
        Set<RelationClassWrapper> relation = manager.getRelation(lamiaExpression);
        if (relation == null || relation.isEmpty()) {
            markerMessagePanel.unassociated(lamiaExpression);
        }
    }

    private LamiaCode getLamaCode(PsiMethodCallExpression psiElement, Project project, boolean isComplete) {
        // 如果已经知道表达式是不完整的 直接返回对应的提示
        if (!isComplete) {
            return new LamiaCode("Invalid expression, please set the corresponding expression correctly. \n" +
                    "You can refer to the document: https://github.com/cao2068959/lamia", false);
        }

        // 用点击的表达式生成对应的转换语句
        LamiaExpressionManager lamiaExpressionManager = LamiaExpressionManager.getInstance(project);
        ConvertResult lamiaConvertResult = lamiaExpressionManager.convert(psiElement);

        // 告诉编译器哪一些类发生了变动需要重新编译
        //BuildManager.getInstance().notifyFilesChanged(Lists.of(new File(canonicalPath)));
        //CompilerManager.getInstance(project).compile();

        if (lamiaConvertResult.isSuccess()) {
            LamiaCode lamiaCode = new LamiaCode(lamiaConvertResult.getData(), true);
            lamiaCode.setImportClassPath(lamiaConvertResult.getImportClassPath());
            return lamiaCode;
        }
        return new LamiaCode(lamiaConvertResult.getMsg(), false);

    }


    private PsiElement getSpiCodeBlock(PsiElement psiElement) {
        PsiElement data = psiElement.getParent();
        while (true) {
            if (data instanceof PsiCodeBlock method) {
                return method;
            }
            if (data == null || data instanceof PsiClass || data instanceof PsiFile) {
                return null;
            }
            data = data.getParent();
        }
    }

}
