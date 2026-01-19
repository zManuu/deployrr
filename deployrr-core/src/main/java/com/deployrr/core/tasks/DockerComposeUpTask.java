package com.deployrr.core.tasks;

import com.deployrr.core.ssh.SSHConnectionImpl;
import com.deployrr.api.task.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Task(name = "DockerComposeUp", keys = {"dockercomposeup", "docker_compose_up", "docker-compose-up"})
public class DockerComposeUpTask extends DeployTask {

    private final static Logger LOG = LogManager.getLogger(DockerComposeUpTask.class);

    @TaskOpt(value = "location", example = "/deployment/docker-compose.yaml")
    private String location;

    public DockerComposeUpTask(SSHConnectionImpl sshConnection, String name) {
        super(sshConnection, name);
    }

    @Override
    public TaskResult execute() throws TaskException {
        LOG.debug("Starting docker-compose in '{}'.", this.location);
        String command = String.format("docker compose -f %s up -d --remove-orphans", this.location);
        try {
            this.sshConnection.executeCommandLogging(command);
            return TaskResult.success();
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

}
