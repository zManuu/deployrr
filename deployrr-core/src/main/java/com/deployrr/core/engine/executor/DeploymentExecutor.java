package com.deployrr.core.engine.executor;

import com.deployrr.api.task.DeployTask;
import com.deployrr.api.task.TaskException;
import com.deployrr.api.task.TaskResult;
import com.deployrr.api.task.validation.TaskValidationHook;
import com.deployrr.core.engine.DeployrrOutput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeploymentExecutor {

    private final static Logger LOG = LogManager.getLogger(DeploymentExecutor.class);
    public static final String SUCCESS = "\u001B[32mSuccess\u001B[0m";
    public static final String FAILURE = "\u001B[31mFailure\u001B[0m";

    private final List<DeployTask> tasks;
    private final Map<DeployTask, List<TaskValidationHook>> validationHooks;

    public DeploymentExecutor(List<DeployTask> tasks, Map<DeployTask, List<TaskValidationHook>> validationHooks) {
        this.tasks = tasks;
        this.validationHooks = validationHooks;
    }

    public boolean deploy() {
        LOG.info("Running deployment.");
        boolean deploySuccess = true;

        // Execute tasks
        for (int i = 0; i < this.tasks.size(); i++) {
            DeployTask task = this.tasks.get(i);
            DeployrrOutput.line(String.format("%s (%s/%s)", task.getDisplayName(), i+1, this.tasks.size()));
            TaskResult taskResult;
            List<TaskValidationHook> taskValidationHooks = task.validationHooks() != null
                    ? task.validationHooks() : new ArrayList<>();

            try {
                taskResult = task.execute();

                if (taskResult.isSuccess()) {
                    LOG.info(SUCCESS);
                } else {
                    LOG.error(FAILURE, taskResult.getException());
                    if (!task.getGeneralOptions().getIgnoreFailure()) {
                        deploySuccess = false;
                        break;
                    }
                }
            } catch (TaskException e) {
                LOG.error(FAILURE, e);
                if (!task.getGeneralOptions().getIgnoreFailure()) {
                    deploySuccess = false;
                    break;
                }
            }

            this.validationHooks.put(task, taskValidationHooks);
            DeployrrOutput.line();
        }

        return deploySuccess;
    }

}
