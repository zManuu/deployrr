package com.deployrr.core.tasks.docker;

import com.deployrr.api.task.*;

import java.io.IOException;

@Task(name = "Docker Compose Pull", keys = {"dockercomposepull", "docker_compose_pull", "docker-compose-pull"},
        description = "Pulls images from a docker-compose.")
public class DockerComposePullTask extends DeployTask {

    @TaskOpt(value = "location", example = "/deployment/docker-compose.yaml", description = "File-path of the docker-compose file.")
    private String location;

    @TaskOpt(value = "ignore_buildable", description = "Ignore images that can be built", required = false, defaultValue = "false")
    private Boolean ignoreBuildable;

    @TaskOpt(value = "ignore_pull_failures", description = "Pull what it can and ignores images with pull failures", required = false, defaultValue = "false")
    private Boolean ignorePullFailures;

    @TaskOpt(value = "include_deps", description = "Also pull services declared as dependencies", required = false, defaultValue = "false")
    private Boolean includeDeps;

    @TaskOpt(value = "policy", description = "Apply pull policy: missing / always", required = false, defaultValue = "missing")
    private String policy;

    @TaskOpt(value = "quiet", description = "Pull without printing progress information", required = false, defaultValue = "false")
    private Boolean quiet;

    public DockerComposePullTask(DeployTaskParameters taskParameters) {
        super(taskParameters);
    }

    @Override
    public TaskResult execute() throws TaskException {
        String command = String.format("docker compose -f %s pull", this.location);
        if (this.ignoreBuildable) {
            command += " --ignore-buildable";
        }
        if (this.ignorePullFailures) {
            command += " --ignore-pull-failures";
        }
        if (this.includeDeps) {
            command += " --include-deps";
        }
        command += " --policy " + this.policy;
        if (this.quiet) {
            command += " --quiet";
        }
        try {
            this.sshConnection.executeCommandLogging(command, this.generalOptions);
            return TaskResult.success();
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

}
