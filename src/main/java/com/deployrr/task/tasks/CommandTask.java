package com.deployrr.task.tasks;

import com.deployrr.ssh.SSHConnection;
import com.deployrr.task.DeployTask;
import com.deployrr.task.Task;
import com.deployrr.task.TaskException;
import com.deployrr.task.TaskOpt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Task(name = "Command", keys = {"cmd", "exec", "command"})
public class CommandTask extends DeployTask {

    private final static Logger LOG = LogManager.getLogger(CommandTask.class);

    @TaskOpt("cmd")
    private String cmd;

    @TaskOpt(value = "cwd", required = false)
    private String cwd;

    public CommandTask(SSHConnection sshConnection, String name) {
        super(sshConnection, name);
    }

    @Override
    public void execute() throws TaskException {
        try {
            String fullCommand = this.cwd != null
                    ? String.format("cd %s && %s", this.cwd, this.cmd)
                    : this.cmd;

            this.sshConnection.executeCommand(fullCommand)
                    .forEach(line -> LOG.info(">> {}", line));
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

}
