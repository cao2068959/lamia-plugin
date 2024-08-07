package org.chy.lamiaplugin.marker.gutter;

import com.chy.lamia.convert.core.entity.*;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiStatement;
import com.intellij.ui.awt.RelativePoint;
import org.chy.lamiaplugin.utlis.IconConstant;
import org.chy.lamiaplugin.utlis.LamiaPsiUtils;
import org.chy.lamiaplugin.utlis.PsiMethodUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

import static org.chy.lamiaplugin.marker.gutter.ErrorTypeConvertButton.HandleEnum.IGNORE_GEN;

public class ErrorTypeConvertButton extends GutterButton {


    private final AbnormalVar abnormalVar;
    private final PsiMethodCallExpression lamiaExpression;

    List<HandleEnum> allHandleType = List.of(HandleEnum.IGNORE, IGNORE_GEN);

    public ErrorTypeConvertButton(AbnormalVar abnormalVar, PsiMethodCallExpression psiElement) {
        super("The converted field types do not match. Click to generate the corresponding conversion statement", IconConstant.PROMPT_ICON);
        this.abnormalVar = abnormalVar;
        this.lamiaExpression = psiElement;
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        showPopup(actionButton, new Point(0, 0));

        //closeBalloon();
    }

    /**
     * 添加一个忽略表达式
     */
    public void addIgnoreExpression() {
        TypeDefinition instanceType = abnormalVar.getInstanceType();
        String text = ".ignoreField(" + instanceType.simpleClassName() + "::" + getGetter(abnormalVar.getVarName()) + ")";
        BuildInfo errorArgBuildInfo = abnormalVar.getErrorMaterial().getProtoMaterialInfo().getBuildInfo();
        LamiaPsiUtils.insertRule(errorArgBuildInfo, text, parentPanel.getProject());

    }

    /**
     * 添加一个转换表达式
     */
    private void addConvertExpression() {


        SimpleMaterialInfo errorMaterial = abnormalVar.getErrorMaterial();
        ProtoMaterialInfo errorProtoMaterialInfo = errorMaterial.getProtoMaterialInfo();
        String errorClass = errorMaterial.getType().simpleClassName();
        String targetClass = abnormalVar.getType().simpleClassName();

        String tempName = "temp" + errorClass;
        String getter = getGetter(abnormalVar.getVarName());
        String expression = targetClass + " " + tempName + " = Lamia.builder().rule().mapping(" + errorProtoMaterialInfo.getMaterial().getText()
                + "." + getter + "()).build(" + targetClass + ".class);";

        Object holder = errorProtoMaterialInfo.getBuildInfo().getHolder();
        if (!(holder instanceof PsiMethodCallExpression callExpression)) {
            return;
        }
        String instanceName = getInstanceNameHandle(callExpression);
        PsiElement psiElement = LamiaPsiUtils.insertCodeAfter(callExpression, expression, parentPanel.getProject());
        String setter = getSetter(abnormalVar.getVarName(), tempName);
        String setterExpression = instanceName + "." + setter + ";";
        LamiaPsiUtils.insertCodeAfter(psiElement, setterExpression, parentPanel.getProject());
    }

    private String getInstanceNameHandle(PsiMethodCallExpression callExpression) {
        PsiStatement statement = PsiMethodUtils.getBelongPsiStatement(callExpression);
        if (statement instanceof PsiReturnStatement) {
            return "result";
        }
        return abnormalVar.getInstanceName();
    }


    private String getGetter(String name) {
        return "get" + superHump(name);
    }

    private String getSetter(String name, String param) {
        return "set" + superHump(name) + "(" + param + ")";
    }

    private String superHump(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public void showPopup(Component component, Point point) {
        ListPopup popup = JBPopupFactory.getInstance().createListPopup(new ErrorTypeConvertSelector());
        popup.show(new RelativePoint(component, point));
    }


    class ErrorTypeConvertSelector extends BaseListPopupStep<HandleEnum> {
        public ErrorTypeConvertSelector() {
            super("error type handle", allHandleType);
        }

        @Override
        public PopupStep onChosen(HandleEnum selectedValue, boolean finalChoice) {
            addIgnoreExpression();

            if (selectedValue == IGNORE_GEN) {
                addConvertExpression();
            }

            closeBalloon();
            return FINAL_CHOICE;
        }


        @Override
        public @NotNull String getTextFor(HandleEnum value) {
            return value.getDesc();
        }
    }


    enum HandleEnum {
        IGNORE("Ignoring fields"),
        IGNORE_GEN("Ignoring fields and generate expression");

        private final String desc;

        HandleEnum(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }
}
