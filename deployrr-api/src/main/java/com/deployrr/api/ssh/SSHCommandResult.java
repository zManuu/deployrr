package com.deployrr.api.ssh;

import java.util.List;

public class SSHCommandResult {

    private final List<String> stdout;
    private final List<String> stderr;
    private final Integer exitCode;

    public SSHCommandResult(List<String> stdout, List<String> stderr, Integer exitCode) {
        this.stdout = stdout;
        this.stderr = stderr;
        this.exitCode = exitCode;
    }

    public List<String> getStdOut() {
        return stdout;
    }

    public List<String> getStdErr() {
        return stderr;
    }

    public Integer getExitCode() {
        return exitCode;
    }

}
