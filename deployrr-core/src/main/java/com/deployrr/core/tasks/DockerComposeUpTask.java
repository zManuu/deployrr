package com.deployrr.core.tasks;

import com.deployrr.api.task.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Task(name = "DockerComposeUp", keys = {"dockercomposeup", "docker_compose_up", "docker-compose-up"})
public class DockerComposeUpTask extends DeployTask {

    private final static Logger LOG = LogManager.getLogger(DockerComposeUpTask.class);

    @TaskOpt(value = "location", example = "/deployment/docker-compose.yaml")
    private String location;

    @TaskOpt(value = "demon", required = false, defaultValue = "true")
    private Boolean demon;

    @TaskOpt(value = "remove_orphans", required = false, defaultValue = "true")
    private Boolean removeOrphans;

    @TaskOpt(value = "pull_policy", required = false, defaultValue = "missing", example = "always")
    private String pullPolicy;

    public DockerComposeUpTask(DeployTaskParameters taskParameters) {
        super(taskParameters);
    }

    @Override
    public TaskResult execute() throws TaskException {
        LOG.debug("Starting docker-compose in '{}'.", this.location);
        String command = String.format("docker compose -f %s up --pull %s", this.location, this.pullPolicy);
        if (this.demon) {
            command += " -d";
        }
        if (this.removeOrphans) {
            command += " --remove-orphans";
        }

        try {
            this.sshConnection.executeCommandLogging(command, this.generalOptions);
            return TaskResult.success();
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

}
