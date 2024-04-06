package org.chy.lamiaplugin.marker.gutter;

import com.chy.lamia.convert.core.entity.AbnormalVar;
import com.chy.lamia.convert.core.entity.BuildInfo;
import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.ListPopupStep;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.ui.awt.RelativePoint;
import org.chy.lamiaplugin.utlis.LamiaPsiUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static org.chy.lamiaplugin.marker.gutter.ErrorTypeConvertButton.HandleEnum.IGNORE_GEN;

public class ErrorTypeConvertButton extends GutterButton {


    private final AbnormalVar abnormalVar;
    private final PsiMethodCallExpression lamiaExpression;

    List<HandleEnum> allHandleType = List.of(HandleEnum.IGNORE, IGNORE_GEN);

    public ErrorTypeConvertButton(AbnormalVar abnormalVar, PsiMethodCallExpression psiElement) {
        super("The converted field types do not match. Click to generate the corresponding conversion statement", AllIcons.Actions.AddList);
        this.abnormalVar = abnormalVar;
        this.lamiaExpression = psiElement;
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        showPopup(actionButton, new Point(0, 0));

        //closeBalloon();
    }

    public void addIgnoreExpression() {

        TypeDefinition instanceType = abnormalVar.getInstanceType();
        String text = ".ignoreField(" + instanceType.simpleClassName() + "::" + getGetter(abnormalVar.getVarName()) + ")";
        BuildInfo errorArgBuildInfo = abnormalVar.getErrorMaterial().getProtoMaterialInfo().getBuildInfo();
        LamiaPsiUtils.insertRule(errorArgBuildInfo, text, parentPanel.getProject());

    }

    private String getGetter(String name) {
        return "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
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
