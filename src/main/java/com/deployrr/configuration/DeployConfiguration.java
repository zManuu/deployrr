package com.deployrr.configuration;

import java.util.List;

public class DeployConfiguration {

    private String deployrrVersion;
    private DeploySSHConfiguration ssh;
    private List<DeployTaskConfiguration> tasks;

    public DeployConfiguration(String deployrrVersion, DeploySSHConfiguration ssh, List<DeployTaskConfiguration> tasks) {
        this.deployrrVersion = deployrrVersion;
        this.ssh = ssh;
        this.tasks = tasks;
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

    public void setDeployrrVersion(String deployrrVersion) {
        this.deployrrVersion = deployrrVersion;
    }

    public void setSsh(DeploySSHConfiguration ssh) {
        this.ssh = ssh;
    }

    public void setTasks(List<DeployTaskConfiguration> tasks) {
        this.tasks = tasks;
    }
}
