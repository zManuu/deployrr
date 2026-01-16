package com.deployrr.task.tasks;

import com.deployrr.ssh.SSHConnection;
import com.deployrr.task.*;

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
    public TaskResult execute() throws TaskException {
        String command = String.format("git clone %s %s", this.url, this.location);
        try {
            this.sshConnection.executeCommandLogging(command);
            return TaskResult.success();
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

}