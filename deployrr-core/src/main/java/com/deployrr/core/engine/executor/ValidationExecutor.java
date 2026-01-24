package com.deployrr.core.engine.executor;

import com.deployrr.api.task.DeployTask;
import com.deployrr.api.task.validation.TaskValidationHook;
import com.deployrr.core.engine.DeployrrOutput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class ValidationExecutor {

    private final static Logger LOG = LogManager.getLogger(ValidationExecutor.class);
    public static final String VALIDATION_FAILURE = "\u001B[31mValidation Failure\u001B[0m";

    private final Map<DeployTask, List<TaskValidationHook>> validationHooks;

    public ValidationExecutor(Map<DeployTask, List<TaskValidationHook>> validationHooks) {
        this.validationHooks = validationHooks;
    }

    public boolean runValidationHooks() {
        LOG.info("Running validation hooks.");
        boolean validationSuccess = true;

        for (Map.Entry<DeployTask, List<TaskValidationHook>> deployTaskListEntry : this.validationHooks.entrySet()) {
            DeployTask task = deployTaskListEntry.getKey();
            List<TaskValidationHook> taskValidationHooks = deployTaskListEntry.getValue();

            if (task.getGeneralOptions().getIgnoreValidationHooks()
                    || taskValidationHooks == null
                    || taskValidationHooks.isEmpty()) {
                continue;
            }

            boolean taskValidationSuccess = true;
            DeployrrOutput.line(String.format("%s: %s", task.getDisplayName(), taskValidationHooks.size()));
            for (TaskValidationHook taskValidationHook : taskValidationHooks) {
                try {
                    taskValidationHook.validate();
                } catch (Exception e) {
                    LOG.error(VALIDATION_FAILURE, e);
                    validationSuccess = false;
                    taskValidationSuccess = false;
                    break;
                }
            }
            DeployrrOutput.line();

            if (!taskValidationSuccess) {
                break;
            }
        }

        return validationSuccess;
    }

}
