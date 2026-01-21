package com.deployrr.core.tasks;

import com.deployrr.api.ssh.SSHCommandResult;
import com.deployrr.api.task.*;
import com.deployrr.api.task.validation.TaskValidationException;
import com.deployrr.api.task.validation.TaskValidationHook;

import java.io.IOException;
import java.util.List;

import static com.deployrr.core.engine.Deployrr.YES;

@Task(name = "Make directory", keys = {"mkdir"}, description = "Creates a directory on the remote.")
public class MkdirTask extends DeployTask {

    @TaskOpt(value = "dir", example = "/deployment")
    private String dir;

    public MkdirTask(DeployTaskParameters taskParameters) {
        super(taskParameters);
    }

    @Override
    public TaskResult execute() throws TaskException {
        String command = String.format("mkdir %s", this.dir);
        try {
            this.sshConnection.executeCommandLogging(command, this.generalOptions);
            return TaskResult.success();
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

    private final TaskValidationHook validate = () -> {
        String validationCommand = String.format("if [ -d %s ]; then\necho \"%s\"\nfi", this.dir, YES);

        // Exec
        SSHCommandResult sshCommandResult;
        try {
            sshCommandResult = this.sshConnection.executeCommandLogging(validationCommand, this.generalOptions);
        } catch (IOException e) {
            throw new TaskValidationException(e);
        }
        String text = String.join(" ", sshCommandResult.getStdOut());

        // Check
        if (!YES.equals(text)) {
            throw new TaskValidationException("Directory does not exist? Got: \"" + text + "\"");
        }
    };

    @Override
    public List<TaskValidationHook> validationHooks() {
        return List.of(this.validate);
    }

}
