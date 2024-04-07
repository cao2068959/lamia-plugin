package org.chy.lamiaplugin.utlis;

import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiUtil;

public class PsiTypeUtils {

    public static boolean isUserDefinedClass(PsiType type) {
        PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(type);
        if (psiClass == null) {
            return false;
        }

        PsiFile containingFile = psiClass.getContainingFile();
        if (containingFile == null) {
            return false;
        }

        VirtualFile virtualFile = containingFile.getVirtualFile();
        if (virtualFile == null) {
            return false;
        }

        ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(psiClass.getProject()).getFileIndex();
        return projectFileIndex.isInSourceContent(virtualFile);
    }

    public static TypeDefinition toTypeDefinition(PsiType psiType) {
        if (psiType instanceof PsiClassReferenceType type) {
            TypeDefinition result = new TypeDefinition(type.rawType().getCanonicalText());
            // 收集泛型
            for (PsiType parameter : type.getParameters()) {
                TypeDefinition typeDefinition = toTypeDefinition(parameter);
                result.addGeneric(typeDefinition);
            }
            return result;
        }

        if (psiType instanceof PsiClassType type) {
            TypeDefinition result = new TypeDefinition(type.rawType().getCanonicalText());
            // 收集泛型
            for (PsiType parameter : type.getParameters()) {
                TypeDefinition typeDefinition = toTypeDefinition(parameter);
                result.addGeneric(typeDefinition);
            }
            return result;
        }

        return new TypeDefinition(psiType.getCanonicalText());
    }

    public static PsiType getType(PsiElement psiElement) {
        if (psiElement == null) {
            return null;
        }

        if (psiElement instanceof PsiMethodCallExpression psiMethodCallExpression) {
            return psiMethodCallExpression.getType();
        }

        if (psiElement instanceof PsiTypeCastExpression psiTypeCastExpression) {
            return psiTypeCastExpression.getType();
        }
        return null;
    }


}
