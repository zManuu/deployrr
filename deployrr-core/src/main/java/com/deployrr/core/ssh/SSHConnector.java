package com.deployrr.core.ssh;

import com.deployrr.api.configuration.DeploySSHConfiguration;
import com.deployrr.api.ssh.SSHConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class SSHConnector {

    private final static Logger LOG = LogManager.getLogger(SSHConnector.class);

    private final DeploySSHConfiguration sshConfiguration;

    public SSHConnector(DeploySSHConfiguration sshConfiguration) {
        this.sshConfiguration = sshConfiguration;
    }

    public SSHConnection establishSSHConnection() throws IOException {
        LOG.info("Initiating SSH connection.");
        SSHConnection sshConnection = new SSHConnectionImpl(this.sshConfiguration);
        sshConnection.initiateConnection();
        return sshConnection;
    }

}
