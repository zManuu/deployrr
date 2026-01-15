package com.deployrr.task.tasks;

import com.deployrr.ssh.SSHConnection;
import com.deployrr.task.DeployTask;
import com.deployrr.task.Task;
import com.deployrr.task.TaskException;
import com.deployrr.task.TaskOpt;

import java.io.IOException;

@Task(name = "Git Pull", keys = {"gitpull", "git-pull", "git_pull"}, description = "Pulls a git repository on the remote.")
public class GitPullTask extends DeployTask {

    @TaskOpt(value = "location", example = "/deployment/deployrr-repo")
    private String location;

    public GitPullTask(SSHConnection sshConnection, String name) {
        super(sshConnection, name);
    }

    @Override
    public void execute() throws TaskException {
        String command = String.format("cd %s && git pull", this.location);
        try {
            this.sshConnection.executeCommandLogging(command);
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

}
