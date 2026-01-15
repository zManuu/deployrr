package com.deployrr.task.tasks;

import com.deployrr.ssh.SSHConnection;
import com.deployrr.task.DeployTask;
import com.deployrr.task.Task;
import com.deployrr.task.TaskException;
import com.deployrr.task.TaskOpt;

import java.io.IOException;

@Task(name = "Command", keys = {"cmd", "exec", "command"}, description = "Executes a command on the remote.")
public class CommandTask extends DeployTask {

    @TaskOpt(value = "cmd", example = "script.sh")
    private String cmd;

    @TaskOpt(value = "cwd", required = false, example = "/deployment/")
    private String cwd;

    public CommandTask(SSHConnection sshConnection, String name) {
        super(sshConnection, name);
    }

    @Override
    public void execute() throws TaskException {
        try {
            String fullCommand = this.cwd != null
                    ? String.format("cd %s && %s", this.cwd, this.cmd)
                    : this.cmd;

            this.sshConnection.executeCommandLogging(fullCommand);
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

}
