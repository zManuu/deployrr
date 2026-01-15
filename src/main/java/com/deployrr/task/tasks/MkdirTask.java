package com.deployrr.task.tasks;

import com.deployrr.ssh.SSHConnection;
import com.deployrr.task.DeployTask;
import com.deployrr.task.Task;
import com.deployrr.task.TaskException;
import com.deployrr.task.TaskOpt;

import java.io.IOException;

@Task(name = "Make directory", keys = {"mkdir"}, description = "Creates a directory on the remote.")
public class MkdirTask extends DeployTask {

    @TaskOpt("dir")
    private String dir;

    public MkdirTask(SSHConnection sshConnection, String name) {
        super(sshConnection, name);
    }

    @Override
    public void execute() throws TaskException {
        String command = String.format("mkdir %s", this.dir);
        try {
            this.sshConnection.executeCommandLogging(command);
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

}
