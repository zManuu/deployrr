package com.deployrr.api.task;

public class DeployTaskGeneralOptions {

    @TaskOpt(value = "ignore_failure", required = false, example = "false", defaultValue = "false")
    private Boolean ignoreFailure;

    public Boolean getIgnoreFailure() {
        return ignoreFailure;
    }

}
