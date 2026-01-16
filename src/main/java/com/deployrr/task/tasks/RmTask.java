package com.deployrr.task.tasks;

import com.deployrr.ssh.SSHConnection;
import com.deployrr.task.*;

import java.io.IOException;

@Task(name = "Remove", keys = {"rm", "remove", "delete"}, description = "Removes a file or directory on the remote.")
public class RmTask extends DeployTask {

    @TaskOpt(value = "location", example = "/deployment")
    private String location;

    @TaskOpt(value = "recursive", required = false, example = "true")
    private Boolean recursive;

    @TaskOpt(value = "force", required = false, example = "true")
    private Boolean force;

    public RmTask(SSHConnection sshConnection, String name) {
        super(sshConnection, name);
    }

    @Override
    public TaskResult execute() throws TaskException {
        try {
            String command = String.format("rm %s", this.location);
            if (this.recursive) {
                command += " -r";
            }
            if (this.force) {
                command += " -f";
            }

            this.sshConnection.executeCommandLogging(command);
            return TaskResult.success();
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }
}
