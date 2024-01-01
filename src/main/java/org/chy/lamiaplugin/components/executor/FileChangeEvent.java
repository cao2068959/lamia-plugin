package org.chy.lamiaplugin.components.executor;

import com.intellij.psi.PsiFile;

public class FileChangeEvent extends Event {

    PsiFile psiFile;

    public FileChangeEvent() {
        super("BuildRefreshExecutor");
    }

    public PsiFile getData() {
        return psiFile;
    }
}
