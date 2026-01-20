package com.deployrr.core.tasks;

import com.deployrr.api.ssh.SSHConnection;
import com.deployrr.api.task.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

@Task(name = "Timeout", keys = {"timeout", "time-out", "sleep"}, description = "Makes the deployment pause for a specified time period.")
public class TimeoutTask extends DeployTask {

    @TaskOpt(value = "time", example = "500")
    private Integer time;

    @TaskOpt(value = "unit", required = false, defaultValue = "MILLISECONDS", example = "SECONDS")
    private String unit;

    private final static Logger LOG = LogManager.getLogger(TimeoutTask.class);

    public TimeoutTask(SSHConnection sshConnection, String name) {
        super(sshConnection, name);
    }

    @Override
    public TaskResult execute() throws TaskException {
        TimeUnit timeUnit;
        try {
            timeUnit = TimeUnit.valueOf(this.unit);
        } catch (IllegalArgumentException e) {
            throw new TaskException("Invalid time unit passed.");
        }
        long millis = timeUnit.toMillis(this.time);
        LOG.debug("Sleeping for {}ms.", millis);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new TaskException("TimeoutTask was interrupted!", e);
        }
        return TaskResult.success();
    }

}
