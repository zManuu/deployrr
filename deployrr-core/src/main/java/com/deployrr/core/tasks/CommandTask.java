package com.deployrr.core.tasks;

import com.deployrr.core.ssh.SSHConnectionImpl;
import com.deployrr.api.task.*;

import java.io.IOException;

@Task(name = "Command", keys = {"cmd", "exec", "command"}, description = "Executes a command on the remote.")
public class CommandTask extends DeployTask {

    @TaskOpt(value = "cmd", example = "script.sh")
    private String cmd;

    @TaskOpt(value = "cwd", required = false, example = "/deployment/")
    private String cwd;

    public CommandTask(SSHConnectionImpl sshConnection, String name) {
        super(sshConnection, name);
    }

    @Override
    public TaskResult execute() throws TaskException {
        try {
            String fullCommand = this.cwd != null
                    ? String.format("cd %s && %s", this.cwd, this.cmd)
                    : this.cmd;

            this.sshConnection.executeCommandLogging(fullCommand);
            return TaskResult.success();
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

}
