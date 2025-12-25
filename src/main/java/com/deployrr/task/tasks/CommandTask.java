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
    private String command;

    public CommandTask(SSHConnection sshConnection, String name) {
        super(sshConnection, name);
    }

    @Override
    public void execute() throws TaskException {
        try {
            this.sshConnection.executeCommand(command)
                    .forEach(line -> LOG.info(">> {}", line));
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

}
