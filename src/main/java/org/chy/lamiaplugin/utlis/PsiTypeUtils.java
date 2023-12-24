package org.chy.lamiaplugin.utlis;

import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiType;
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

}
