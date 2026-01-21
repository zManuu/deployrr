package com.deployrr.api.task;

public class DeployTaskGeneralOptions {

    @TaskOpt(
            value = "ignore_failure",
            required = false, example = "false", defaultValue = "false",
            description = "Ignore the potential failure of a task and continue with the next task."
    )
    private Boolean ignoreFailure;

    @TaskOpt(
            value = "ignore_validation_hooks",
            required = false, example = "false", defaultValue = "false",
            description = "Ignore all validation hooks of a task (for instance, whether the file exists)."
    )
    private Boolean ignoreValidationHooks;

    public Boolean getIgnoreFailure() {
        return ignoreFailure;
    }

    public Boolean getIgnoreValidationHooks() {
        return ignoreValidationHooks;
    }

}
