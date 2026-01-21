package com.deployrr.api.task;

import com.deployrr.api.ssh.SSHConnection;

public abstract class DeployTask {

    protected final SSHConnection sshConnection;
    protected final String name;
    protected final DeployTaskGeneralOptions generalOptions;

    protected DeployTask(DeployTaskParameters taskParameters) {
        this.sshConnection = taskParameters.getSshConnection();
        this.name = taskParameters.getName();
        this.generalOptions = taskParameters.getGeneralOptions();
    }

    public abstract TaskResult execute() throws TaskException;

    public String getDisplayName() {
        String taskName = getClass().getAnnotation(Task.class).name();
        return name != null && !name.isEmpty()
                ? String.format("%s (%s)", name, taskName)
                : taskName;
    }

    public DeployTaskGeneralOptions getGeneralOptions() {
        return generalOptions;
    }
    
}
