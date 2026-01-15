package com.deployrr.task.tasks;

import com.deployrr.ssh.SSHConnection;
import com.deployrr.task.DeployTask;
import com.deployrr.task.Task;
import com.deployrr.task.TaskException;
import com.deployrr.task.TaskOpt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Task(name = "CopyFile", keys = {"cp", "scp", "copy"}, description = "Copies a local file or directory to the remote.")
public class CopyFileTask extends DeployTask {

    private final static Logger LOG = LogManager.getLogger(CopyFileTask.class);

    @TaskOpt(value = "source", example = "deployment/start.sh")
    private String source;

    @TaskOpt(value = "target", example = "/deployment/")
    private String target;

    @TaskOpt(value = "chmod", required = false, example = "+x")
    private String chmod;

    public CopyFileTask(SSHConnection sshConnection, String name) {
        super(sshConnection, name);
    }

    @Override
    public void execute() throws TaskException {
        LOG.info("Executing copy from '{}' to '{}'.", this.source, this.target);
        try {
            this.sshConnection.copyFile(this.source, this.target);

            if (this.chmod != null) {
                String targetFile = this.target.endsWith("/")
                        ? this.target + this.source.substring(this.source.lastIndexOf("/") + 1)
                        : this.target;
                String command = String.format("chmod %s %s", this.chmod, targetFile);
                this.sshConnection.executeCommandLogging(command);
            }
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

}
