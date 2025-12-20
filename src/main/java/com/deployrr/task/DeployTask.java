package com.deployrr.task;

import com.deployrr.ssh.SSHConnection;

public abstract class DeployTask {

    protected final SSHConnection sshConnection;

    protected DeployTask(SSHConnection sshConnection) {
        this.sshConnection = sshConnection;
    }

    public abstract void execute() throws TaskException;

}
