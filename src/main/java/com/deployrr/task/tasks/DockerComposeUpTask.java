package com.deployrr.task.tasks;

import com.deployrr.ssh.SSHConnection;
import com.deployrr.task.DeployTask;
import com.deployrr.task.Task;
import com.deployrr.task.TaskException;
import com.deployrr.task.TaskOpt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Task(name = "DockerComposeUp", keys = {"dockercomposeup", "docker_compose_up", "docker-compose-up"})
public class DockerComposeUpTask extends DeployTask {

    private final static Logger LOG = LogManager.getLogger(DockerComposeUpTask.class);

    @TaskOpt("location")
    private String location;

    public DockerComposeUpTask(SSHConnection sshConnection, String name) {
        super(sshConnection, name);
    }

    @Override
    public void execute() throws TaskException {
        LOG.info("Starting docker-compose in '{}'.", this.location);
        String command = String.format("docker compose -f %s up -d --remove-orphans", this.location);
        try {
            this.sshConnection.executeCommandLogging(command);
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

}
