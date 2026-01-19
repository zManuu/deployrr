package com.deployrr.pluginexample;

import com.deployrr.api.ssh.SSHConnection;
import com.deployrr.api.task.DeployTask;
import com.deployrr.api.task.Task;
import com.deployrr.api.task.TaskException;
import com.deployrr.api.task.TaskResult;

@Task(name = "Custom Task", keys = {"custom-task", "custom_task", "ct"}, description = "A custom task!")
public class CustomTask extends DeployTask {

    public CustomTask(SSHConnection sshConnection, String name) {
        super(sshConnection, name);
    }

    @Override
    public TaskResult execute() throws TaskException {
        System.out.println("Hello world! (task execution)");

        /*
        try {
            // Upload a file (with SFTP)
            this.sshConnection.copyFile("$USER/Desktop/test.txt", "/deployment");

            // Execute a command
            this.sshConnection.executeCommandLogging("ls /deployment");
        } catch (IOException e) {
            throw new TaskException(e);
        }
        */

        return TaskResult.success();
    }

}
