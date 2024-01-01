package org.chy.lamiaplugin.components.executor;

import com.intellij.compiler.server.BuildManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildRefreshExecutor implements BatchExecutor<FileChangeEvent> {
    @Override
    public String group() {
        return "BuildRefreshExecutor";
    }

    @Override
    public void batchRun(List<FileChangeEvent> events) {

        Map<String, File> handlerData = new HashMap<>();
        for (FileChangeEvent event : events) {
            PsiFile psiFile = event.getData();
            VirtualFile virtualFile = psiFile.getVirtualFile();
            if (virtualFile == null) {
                return;
            }
            String path = virtualFile.getCanonicalPath();
            if (path == null || handlerData.containsKey(path)) {
                return;
            }
            handlerData.put(path, new File(path));
        }

        List<File> files = handlerData.values().stream().toList();
        BuildManager.getInstance().notifyFilesChanged(files);
    }

}
