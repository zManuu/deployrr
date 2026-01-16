package com.deployrr.task;

import com.deployrr.ssh.SSHConnection;

public abstract class DeployTask {

    protected final SSHConnection sshConnection;
    protected final String name;

    protected DeployTask(SSHConnection sshConnection, String name) {
        this.sshConnection = sshConnection;
        this.name = name;
    }

    public abstract TaskResult execute() throws TaskException;

    public String getDisplayName() {
        String taskName = getClass().getAnnotation(Task.class).name();
        return name != null && !name.isEmpty()
                ? String.format("%s (%s)", name, taskName)
                : taskName;
    }

}
