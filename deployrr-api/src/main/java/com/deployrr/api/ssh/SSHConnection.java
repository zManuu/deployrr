package com.deployrr.api.ssh;

import java.io.IOException;
import java.util.List;

public interface SSHConnection {

    void initiateConnection() throws IOException;
    void shutdownConnection() throws IOException;
    void executeCommandLogging(String command) throws IOException;
    List<String> executeCommand(String command) throws IOException;
    void copyFile(String source, String target) throws IOException;

}
