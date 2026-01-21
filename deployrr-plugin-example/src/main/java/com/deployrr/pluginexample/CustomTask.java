package com.deployrr.pluginexample;

import com.deployrr.api.task.*;
import com.deployrr.api.task.validation.TaskValidationException;
import com.deployrr.api.task.validation.TaskValidationHook;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Task(name = "Custom Task", keys = {"custom-task", "custom_task", "ct"}, description = "A custom task!")
public class CustomTask extends DeployTask {

    /*
        // Implementing options for your task.
        @TaskOpt(value = "option", required = false, defaultValue = "Hello world!")
        private String option;
     */

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
            this.sshConnection.executeCommandLogging("ls /deployment", this.generalOptions);
        } catch (IOException e) {
            throw new TaskException(e);
        }
        */

        return TaskResult.success();
    }

    /**
     * You can also implement custom validation hooks for your tasks.
     * They get executed after all deployment is done.
     */
    @Override
    public List<TaskValidationHook> validationHooks() {
        return Collections.singletonList(() -> {
            try {
                if (this.sshConnection.executeCommand("TEST").getExitCode() != 0) {
                    throw new TaskValidationException("Unsuccess.");
                }
            } catch (IOException e) {
                throw new TaskValidationException(e);
            }
        });
    }

}
