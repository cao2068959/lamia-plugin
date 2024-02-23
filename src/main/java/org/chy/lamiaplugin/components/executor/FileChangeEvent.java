package org.chy.lamiaplugin.components.executor;

import com.intellij.psi.PsiFile;

public class FileChangeEvent extends Event {

    PsiFile psiFile;

    public FileChangeEvent(PsiFile psiFile) {
        super("BuildRefreshExecutor");
        this.psiFile = psiFile;
    }

    public PsiFile getData() {
        return psiFile;
    }
}
