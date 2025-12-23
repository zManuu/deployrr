package com.deployrr.configuration;

import com.deployrr.configuration.env.EnvInject;
import com.deployrr.configuration.env.EnvInjectType;

import java.util.Map;

public class DeployTaskConfiguration {

    @EnvInject(EnvInjectType.STRING)
    private String task;

    @EnvInject(EnvInjectType.STRING_MAP)
    private Map<String, String> opt;

    @EnvInject(EnvInjectType.STRING)
    private String name;

    public DeployTaskConfiguration(String task, Map<String, String> opt) {
        this.task = task;
        this.opt = opt;
    }

    public DeployTaskConfiguration(String task, Map<String, String> opt, String name) {
        this.task = task;
        this.opt = opt;
        this.name = name;
    }

    public DeployTaskConfiguration() {
    }

    public String getTask() {
        return task;
    }

    public Map<String, String> getOpt() {
        return opt;
    }

    public String getName() {
        return name;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setOpt(Map<String, String> opt) {
        this.opt = opt;
    }

    public void setName(String name) {
        this.name = name;
    }

}
