package com.deployrr.core.tasks;

import com.deployrr.api.ssh.SSHCommandResult;
import com.deployrr.api.ssh.SSHConnection;
import com.deployrr.api.task.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.IOException;

@Task(name = "Health Check", keys = {"health", "healthcheck", "health_check", "health-check"})
public class HealthCheckTask extends DeployTask {

    private static final Logger LOG = LogManager.getLogger(HealthCheckTask.class);

    @TaskOpt(value = "url", required = false, example = "http://localhost:8080/health")
    private String url;

    @TaskOpt(value = "method", required = false, example = "GET", defaultValue = "GET")
    private String method;

    @TaskOpt(value = "port", required = false, example = "8080")
    private Integer port;

    @TaskOpt(value = "path", required = false, example = "health")
    private String path;

    @TaskOpt(value = "expected", required = false, example = "200", defaultValue = "200")
    private Integer expectedStatusCode;

    public HealthCheckTask(SSHConnection sshConnection, String name) {
        super(sshConnection, name);
    }

    @Override
    public TaskResult execute() throws TaskException {
        String command = resolveCommand();

        // Exec
        SSHCommandResult sshCommandResult;
        try {
            sshCommandResult = this.sshConnection.executeCommandLogging(command);
        } catch (IOException e) {
            throw new TaskException(e);
        }
        String text = String.join(" ", sshCommandResult.getStdOut());

        // Check
        int statusCode;
        try {
            statusCode = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new TaskException("The CURL result did not meet the required format.", e);
        }
        if (!this.expectedStatusCode.equals(statusCode)) {
            throw new TaskException("Got bad status code " + statusCode + ". Expected " + this.expectedStatusCode + ".");
        }
        return TaskResult.success();
    }

    @Nonnull
    private String resolveCommand() {
        String cleanPath = this.path != null
                ? this.path.startsWith("/") ? this.path.substring(1) : this.path
                : "";
        String url = this.url != null
                ? this.url
                : String.format("http://localhost:%s/%s", this.port, cleanPath);

        return "curl -s -o /dev/null -w \"%{http_code}\" -X " + this.method + " " + url;
    }

}
