package com.deployrr.task.tasks;

import com.deployrr.ssh.SSHConnection;
import com.deployrr.task.*;

import java.io.IOException;

@Task(name = "Make directory", keys = {"mkdir"}, description = "Creates a directory on the remote.")
public class MkdirTask extends DeployTask {

    @TaskOpt(value = "dir", example = "/deployment")
    private String dir;

    public MkdirTask(SSHConnection sshConnection, String name) {
        super(sshConnection, name);
    }

    @Override
    public TaskResult execute() throws TaskException {
        String command = String.format("mkdir %s", this.dir);
        try {
            this.sshConnection.executeCommandLogging(command);
            return TaskResult.success();
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

}
