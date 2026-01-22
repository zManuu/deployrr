package com.deployrr.core.tasks.git;

import com.deployrr.api.task.*;

import java.io.IOException;

@Task(name = "Git Pull", keys = {"gitpull", "git-pull", "git_pull"}, description = "Pulls a git repository on the remote.")
public class GitPullTask extends DeployTask {

    @TaskOpt(value = "location", example = "/deployment/deployrr-repo")
    private String location;

    public GitPullTask(DeployTaskParameters taskParameters) {
        super(taskParameters);
    }

    @Override
    public TaskResult execute() throws TaskException {
        String command = String.format("cd %s && git pull", this.location);
        try {
            this.sshConnection.executeCommandLogging(command, this.generalOptions);
            return TaskResult.success();
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

}
