package com.deployrr.api.ssh;

import java.io.IOException;

public interface SSHConnection {

    void initiateConnection() throws IOException;
    void shutdownConnection() throws IOException;
    SSHCommandResult executeCommandLogging(String command) throws IOException;
    SSHCommandResult executeCommand(String command) throws IOException;
    void copyFile(String source, String target) throws IOException;

}
