package com.deployrr.task.tasks;

import com.deployrr.ssh.SSHConnection;
import com.deployrr.task.DeployTask;
import com.deployrr.task.Task;
import com.deployrr.task.TaskException;
import com.deployrr.task.TaskOpt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

@Task(name = "Health Check", keys = {"health", "healthcheck", "health_check", "health-check"})
public class HealthCheckTask extends DeployTask {

    private static final String DEFAULT_METHOD = "GET";
    private static final Logger LOG = LogManager.getLogger(HealthCheckTask.class);

    @TaskOpt(value = "url", required = false)
    private String url;

    @TaskOpt(value = "method", required = false)
    private String method;

    @TaskOpt(value = "port", required = false)
    private Integer port;

    @TaskOpt(value = "path", required = false)
    private String path;

    @TaskOpt(value = "expected", required = false)
    private Integer expectedStatusCode;

    public HealthCheckTask(SSHConnection sshConnection, String name) {
        super(sshConnection, name);
    }

    @Override
    public void execute() throws TaskException {
        String command = resolveCommand();

        // Exec
        List<String> lines;
        try {
            lines = this.sshConnection.executeCommand(command);
        } catch (IOException e) {
            throw new TaskException(e);
        }
        String text = String.join(" ", lines);
        LOG.info(">> {}", text);

        // Check
        int statusCode;
        try {
            statusCode = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new TaskException("The CURL result did not meet the required format.", e);
        }
        if (this.expectedStatusCode != null) {
            if (!this.expectedStatusCode.equals(statusCode)) {
                throw new TaskException("Got bad status code " + statusCode + ". Expected " + this.expectedStatusCode + ".");
            }
        } else {
            if (statusCode < 200 || statusCode >= 300) {
                throw new TaskException("Got bad status code " + statusCode + ".");
            }
        }
    }

    @Nonnull
    private String resolveCommand() {
        String method = this.method != null ? this.method : DEFAULT_METHOD;
        String cleanPath = this.path != null
                ? this.path.startsWith("/") ? this.path.substring(1) : this.path
                : "";
        String url = this.url != null
                ? this.url
                : String.format("http://localhost:%s/%s", this.port, cleanPath);

        return "curl -s -o /dev/null -w \"%{http_code}\" -X " + method + " " + url;
    }

}
