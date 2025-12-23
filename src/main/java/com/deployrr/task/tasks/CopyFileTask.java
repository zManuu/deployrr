package com.deployrr.task.tasks;

import com.deployrr.ssh.SSHConnection;
import com.deployrr.task.DeployTask;
import com.deployrr.task.Task;
import com.deployrr.task.TaskException;
import com.deployrr.task.TaskOpt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Task(name = "CopyFile", keys = {"cp", "scp", "copy"})
public class CopyFileTask extends DeployTask {

    private final static Logger LOG = LogManager.getLogger();

    @TaskOpt("source")
    private String source;

    @TaskOpt("target")
    private String target;

    public CopyFileTask(SSHConnection sshConnection, String name) {
        super(sshConnection, name);
    }

    @Override
    public void execute() throws TaskException {
        LOG.info("Executing copy from '{}' to '{}'.", this.source, this.target);
        try {
            this.sshConnection.copyFile(this.source, this.target);
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

}
