package com.deployrr.api.task;

import com.deployrr.api.ssh.SSHConnection;

public class DeployTaskParameters {

    private final SSHConnection sshConnection;
    private final String name;
    private final DeployTaskGeneralOptions generalOptions;

    public DeployTaskParameters(SSHConnection sshConnection, String name, DeployTaskGeneralOptions generalOptions) {
        this.sshConnection = sshConnection;
        this.name = name;
        this.generalOptions = generalOptions;
    }

    public SSHConnection getSshConnection() {
        return sshConnection;
    }

    public String getName() {
        return name;
    }

    public DeployTaskGeneralOptions getGeneralOptions() {
        return generalOptions;
    }

}
