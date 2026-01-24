package com.deployrr.api.configuration;

import com.deployrr.api.configuration.env.EnvInject;
import com.deployrr.api.configuration.env.EnvInjectType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DeployTaskConfiguration {

    @EnvInject(EnvInjectType.STRING)
    private String task;

    @EnvInject(EnvInjectType.STRING_MAP)
    private Map<String, String> opt;

    @EnvInject(EnvInjectType.STRING)
    private String name;

    private List<String> depends;

    public DeployTaskConfiguration(String task, Map<String, String> opt) {
        this.task = task;
        this.opt = opt;
    }

    public DeployTaskConfiguration(String task, Map<String, String> opt, String name) {
        this.task = task;
        this.opt = opt;
        this.name = name;
    }

    public DeployTaskConfiguration(String task, Map<String, String> opt, String name, List<String> depends) {
        this.task = task;
        this.opt = opt;
        this.name = name;
        this.depends = depends;
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

    public List<String> getDepends() {
        return this.depends != null
                ? this.depends
                : Collections.emptyList();
    }

    public void setDepends(List<String> depends) {
        this.depends = depends;
    }

    @Override
    public String toString() {
        return "DeployTaskConfiguration{" +
                "task='" + task + '\'' +
                ", opt=" + opt +
                ", name='" + name + '\'' +
                ", depends=" + depends +
                '}';
    }

}
