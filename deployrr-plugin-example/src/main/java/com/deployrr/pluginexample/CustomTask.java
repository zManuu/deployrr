package com.deployrr.pluginexample;

import com.deployrr.api.task.*;

@Task(name = "Custom Task", keys = {"custom-task", "custom_task", "ct"}, description = "A custom task!")
public class CustomTask extends DeployTask {

    public CustomTask(DeployTaskParameters taskParameters) {
        super(taskParameters);
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
