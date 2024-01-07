package org.chy.lamiaplugin.components.executor;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethodCallExpression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 当 Lamia 的表达式发生改变的时候 执行
 */
public class LamiaExpressionChangeExecutor implements BatchExecutor<LamiaExpressionChangeEvent> {

    private static final Logger LOG = Logger.getInstance(LamiaExpressionChangeExecutor.class);

    Project project;

    public LamiaExpressionChangeExecutor(Project project) {
        this.project = project;
    }

    @Override
    public String group() {
        return "LamiaExpressionChangeExecutor";
    }




    @Override
    public void batchRun(List<LamiaExpressionChangeEvent> events) {
        Map<PsiMethodCallExpression, LamiaExpressionChangeEvent> pendingList = new HashMap<>();
        for (LamiaExpressionChangeEvent event : events) {
            LamiaExpressionChangeEvent existsEvent = pendingList.get(event.getLamiaExpression());
            // 有重复的情况下 取最新的一个
            if (existsEvent == null || existsEvent.startTime < event.startTime){
                pendingList.put(event.getLamiaExpression(), event);
            }
        }

        // 开始重新处理表达式
        LOG.info("执行---->");

    }

    @Override
    public int minLatency() {
        return 3000;
    }
}
