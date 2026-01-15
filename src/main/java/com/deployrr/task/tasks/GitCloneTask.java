package com.deployrr.task.tasks;

import com.deployrr.ssh.SSHConnection;
import com.deployrr.task.DeployTask;
import com.deployrr.task.Task;
import com.deployrr.task.TaskException;
import com.deployrr.task.TaskOpt;

import java.io.IOException;

@Task(name = "Git Clone", keys = {"gitclone", "git-clone", "git_clone"}, description = "Clones a git repository on the remote.")
public class GitCloneTask extends DeployTask {

    @TaskOpt(value = "url", example = "https://github.com/zManuu/deployrr.git")
    private String url;

    @TaskOpt(value = "location", example = "/deployment/deployrr-repo")
    private String location;

    public GitCloneTask(SSHConnection sshConnection, String name) {
        super(sshConnection, name);
    }

    @Override
    public void execute() throws TaskException {
        String command = String.format("git clone %s %s", this.url, this.location);
        try {
            this.sshConnection.executeCommandLogging(command);
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

}