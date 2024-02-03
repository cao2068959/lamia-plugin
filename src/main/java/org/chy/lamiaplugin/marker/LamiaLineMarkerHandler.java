package org.chy.lamiaplugin.marker;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.*;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBScrollPane;
import org.chy.lamiaplugin.expression.ConvertResult;
import org.chy.lamiaplugin.expression.LamiaExpressionManager;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bignosecat
 */
public class LamiaLineMarkerHandler {

    private static Map<Project, LamiaLineMarkerHandler> instanceList = new ConcurrentHashMap<>();
    private final EditorTextField editorTextField;
    private final JBScrollPane jbScrollPane;

    public static PsiElement psiElement;

    public static LamiaLineMarkerHandler of(Project project) {
        return instanceList.computeIfAbsent(project, LamiaLineMarkerHandler::new);
    }

    public LamiaLineMarkerHandler(Project project) {
        this.editorTextField = new EditorTextField(null, project, JavaFileType.INSTANCE, true);
        editorTextField.setOneLineMode(false);
        this.jbScrollPane = new JBScrollPane(editorTextField);

    }

    public void click(MouseEvent event, PsiMethodCallExpression lamiaMethod, boolean isComplete) {
        showTip("出现----->", event, lamiaMethod, isComplete);
    }


    private void showTip(String msg, MouseEvent event, PsiMethodCallExpression psiElement, boolean isComplete) {
        LamiaLineMarkerHandler.psiElement = psiElement;

        PsiElement parentMethod = getSpiCodeBlock(psiElement);
        Project project = psiElement.getProject();
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);

        // 用Lamia表达式生成对应的代码
        String lamiaCode = getLamaCode(psiElement, project, isComplete);

        // 把这个转换语句放入代码块中用于显示
        JavaCodeFragmentFactory fragmentFactory = JavaCodeFragmentFactory.getInstance(project);
        JavaCodeFragment code = fragmentFactory.createCodeBlockCodeFragment(lamiaCode, parentMethod, true);
        //code.addImportsFromString();

        Document document = documentManager.getDocument(code);

        editorTextField.setDocument(document);


        ApplicationManager.getApplication().invokeLater(() -> {
            JBPopupFactory.getInstance().createBalloonBuilder(jbScrollPane)
                    .setAnimationCycle(10)
                    .setBorderColor(Color.GRAY)
                    .setFillColor(Color.GRAY)
                    .createBalloon().show(new RelativePoint(event), Balloon.Position.below);
        });
        //.show(new RelativePoint(event), Balloon.Position.below));
    }

    private String getLamaCode(PsiMethodCallExpression psiElement, Project project, boolean isComplete) {
        // 如果已经知道表达式是不完整的 直接返回对应的提示
        if (!isComplete) {
            return "Invalid expression, please set the corresponding expression correctly. \n" +
                    "You can refer to the document: https://github.com/cao2068959/lamia";
        }

        // 用点击的表达式生成对应的转换语句
        LamiaExpressionManager lamiaExpressionManager = LamiaExpressionManager.getInstance(project);
        ConvertResult lamiaConvertResult = lamiaExpressionManager.convert(psiElement);

        // 告诉编译器哪一些类发生了变动需要重新编译
        //BuildManager.getInstance().notifyFilesChanged(Lists.of(new File(canonicalPath)));
        //CompilerManager.getInstance(project).compile();

        if (lamiaConvertResult.isSuccess()) {
            return lamiaConvertResult.getData();
        }
        return lamiaConvertResult.getMsg();

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
