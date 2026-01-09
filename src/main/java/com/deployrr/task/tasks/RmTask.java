package com.deployrr.task.tasks;

import com.deployrr.ssh.SSHConnection;
import com.deployrr.task.DeployTask;
import com.deployrr.task.Task;
import com.deployrr.task.TaskException;
import com.deployrr.task.TaskOpt;

import java.io.IOException;

@Task(name = "Remove", keys = {"rm", "remove", "delete"})
public class RmTask extends DeployTask {

    @TaskOpt("location")
    private String location;

    @TaskOpt(value = "recursive", required = false)
    private Boolean recursive;

    @TaskOpt(value = "force", required = false)
    private Boolean force;

    public RmTask(SSHConnection sshConnection, String name) {
        super(sshConnection, name);
    }

    @Override
    public void execute() throws TaskException {
        try {
            String command = String.format("rm %s", this.location);
            if (this.recursive) {
                command += " -r";
            }
            if (this.force) {
                command += " -f";
            }

            this.sshConnection.executeCommandLogging(command);
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }
}
