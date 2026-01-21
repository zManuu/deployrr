package com.deployrr.api.task;

public class DeployTaskGeneralOptions {

    @TaskOpt(
            value = "ignore_failure",
            required = false, example = "false", defaultValue = "false",
            description = "Ignore the potential failure of a task and continue with the next task."
    )
    private Boolean ignoreFailure;

    public Boolean getIgnoreFailure() {
        return ignoreFailure;
    }

}
