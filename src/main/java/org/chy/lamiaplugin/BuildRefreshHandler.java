package org.chy.lamiaplugin;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerTopics;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BuildRefreshHandler implements ProjectManagerListener {
    private final Project project;
    private long lastBuildTime = -1;
    private static Map<Project, BuildRefreshHandler> instances = new HashMap<>();

    private Map<String, Long> fileRefreshTime = new ConcurrentHashMap<>();

    public BuildRefreshHandler(Project project) {
        this.project =project;
        MessageBus messageBus = project.getMessageBus();
        MessageBusConnection connection = messageBus.connect();
        connection.subscribe(CompilerTopics.COMPILATION_STATUS, new CompilationStatusListener() {
            @Override
            public void compilationFinished(boolean aborted, int errors, int warnings, CompileContext compileContext) {
                lastBuildTime = System.currentTimeMillis();
            }
        });
    }

    public static void listen(Project project) {
        BuildRefreshHandler buildListener = new BuildRefreshHandler(project);
        instances.put(project, buildListener);
    }

    public static BuildRefreshHandler getInstance(Project project) {
        return instances.get(project);
    }


    public void refresh(PsiFile file) {
        if (!(file.getFileType() instanceof JavaFileType)) {
            return;
        }
        VirtualFile virtualFile = file.getVirtualFile();
        if (virtualFile == null){
            return;
        }
        String canonicalPath = virtualFile.getCanonicalPath();

        Long lastRefresh = fileRefreshTime.get(canonicalPath);
        // 在下一次编译之前已经刷新过 不需要再次刷新
        if (lastRefresh != null && lastRefresh > lastBuildTime) {
            return;
        }
        doRefresh(virtualFile);
        fileRefreshTime.put(canonicalPath, System.currentTimeMillis());
    }

    private void doRefresh(VirtualFile virtualFile) {
        ApplicationManager.getApplication().invokeLater(() -> WriteCommandAction.runWriteCommandAction(project, () -> {
            Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
            if (document != null) {
                int textLength = document.getTextLength();
                document.insertString(textLength, " ");
                FileDocumentManager.getInstance().saveDocumentAsIs(document);

                document.deleteString(textLength, textLength + 1);
                FileDocumentManager.getInstance().saveDocumentAsIs(document);
            }
        }));


    }


}
