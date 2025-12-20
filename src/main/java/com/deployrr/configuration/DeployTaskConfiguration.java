package com.deployrr.configuration;

import java.util.Map;

public class DeployTaskConfiguration {

    private String task;
    private Map<String, String> opt;

    public DeployTaskConfiguration(String task, Map<String, String> opt) {
        this.task = task;
        this.opt = opt;
    }

    public DeployTaskConfiguration() {
    }

    public String getTask() {
        return task;
    }

    public Map<String, String> getOpt() {
        return opt;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setOpt(Map<String, String> opt) {
        this.opt = opt;
    }
}
