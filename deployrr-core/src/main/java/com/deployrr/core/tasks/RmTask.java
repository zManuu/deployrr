package com.deployrr.core.tasks;

import com.deployrr.api.ssh.SSHCommandResult;
import com.deployrr.api.task.*;
import com.deployrr.api.task.validation.TaskValidationException;
import com.deployrr.api.task.validation.TaskValidationHook;

import java.io.IOException;
import java.util.List;

import static com.deployrr.core.engine.Deployrr.YES;

@Task(name = "Remove", keys = {"rm", "remove", "delete"}, description = "Removes a file or directory on the remote.")
public class RmTask extends DeployTask {

    @TaskOpt(value = "location", example = "/deployment")
    private String location;

    @TaskOpt(value = "recursive", required = false, example = "true")
    private Boolean recursive;

    @TaskOpt(value = "force", required = false, example = "true")
    private Boolean force;

    public RmTask(DeployTaskParameters taskParameters) {
        super(taskParameters);
    }

    @Override
    public TaskResult execute() throws TaskException {
        try {
            String command = String.format("rm %s", this.location);
            if (this.recursive) {
                command += " -r";
            }
            if (this.force) {
                command += " -f";
            }

            this.sshConnection.executeCommandLogging(command, this.generalOptions);
            return TaskResult.success();
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

    private final TaskValidationHook validate = () -> {
        String validationCommand = String.format("if [ -e %s ]; then\necho \"%s\"\nfi", this.location, YES);

        // Exec
        SSHCommandResult sshCommandResult;
        try {
            sshCommandResult = this.sshConnection.executeCommandLogging(validationCommand, this.generalOptions);
        } catch (IOException e) {
            throw new TaskValidationException(e);
        }
        String text = String.join(" ", sshCommandResult.getStdOut());

        // Check
        if (YES.equals(text)) {
            throw new TaskValidationException("File/directory does exist? Got: \"" + text + "\"");
        }
    };

    @Override
    public List<TaskValidationHook> validationHooks() {
        return List.of(this.validate);
    }

}
