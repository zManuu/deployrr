package com.deployrr.api.ssh;

import com.deployrr.api.task.DeployTaskGeneralOptions;

import java.io.IOException;

public interface SSHConnection {

    void initiateConnection() throws IOException;
    void shutdownConnection() throws IOException;
    SSHCommandResult executeCommandLogging(String command, DeployTaskGeneralOptions generalOptions) throws IOException;
    SSHCommandResult executeCommand(String command) throws IOException;
    void copyFile(String source, String target) throws IOException;

}
