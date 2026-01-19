package com.deployrr.api.configuration;

import com.deployrr.api.configuration.env.EnvInject;
import com.deployrr.api.configuration.env.EnvInjectType;

import java.util.List;
import java.util.Map;

public class DeployConfiguration {

    @EnvInject(EnvInjectType.STRING)
    private String deployrrVersion;

    @EnvInject(EnvInjectType.OBJECT)
    private DeploySSHConfiguration ssh;

    @EnvInject(EnvInjectType.OBJECT_LIST)
    private List<DeployTaskConfiguration> tasks;

    private Map<String, String> variables;

    public DeployConfiguration(String deployrrVersion, DeploySSHConfiguration ssh, List<DeployTaskConfiguration> tasks, Map<String, String> variables) {
        this.deployrrVersion = deployrrVersion;
        this.ssh = ssh;
        this.tasks = tasks;
        this.variables = variables;
    }

    public DeployConfiguration() {
    }

    public String getDeployrrVersion() {
        return deployrrVersion;
    }

    public DeploySSHConfiguration getSsh() {
        return ssh;
    }

    public List<DeployTaskConfiguration> getTasks() {
        return tasks;
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public void setDeployrrVersion(String deployrrVersion) {
        this.deployrrVersion = deployrrVersion;
    }

    public void setSsh(DeploySSHConfiguration ssh) {
        this.ssh = ssh;
    }

    public void setTasks(List<DeployTaskConfiguration> tasks) {
        this.tasks = tasks;
    }

    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }

}
