package com.deployrr.core.ssh;

import com.deployrr.api.configuration.DeploySSHConfiguration;
import com.deployrr.api.ssh.SSHConnection;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SSHConnectionImpl implements SSHConnection {

    private final static Logger LOG = LogManager.getLogger(SSHConnectionImpl.class);
    private final DeploySSHConfiguration sshConfiguration;
    private SSHClient sshClient;

    public SSHConnectionImpl(DeploySSHConfiguration sshConfiguration) {
        this.sshConfiguration = sshConfiguration;
    }

    public void initiateConnection() throws IOException {
        if (this.sshClient != null && this.sshClient.isConnected()) {
            this.sshClient.disconnect();
            this.sshClient = null;
        }
        if (this.sshConfiguration.getPassword() != null && this.sshConfiguration.getPrivateKey() != null) {
            throw new IOException("Impossible configuration. SSH authentication can only be performed with either password or private-key.");
        }

        this.sshClient = new SSHClient();
        this.sshClient.addHostKeyVerifier(new PromiscuousVerifier());
        this.sshClient.connect(this.sshConfiguration.getHost());

        // AUTH
        if (this.sshConfiguration.getPassword() != null) {
            this.sshClient.authPassword(
                    this.sshConfiguration.getUser(),
                    this.sshConfiguration.getPassword()
            );
        } else if (this.sshConfiguration.getPrivateKey() != null) {
            KeyProvider keys = this.sshClient.loadKeys(this.sshConfiguration.getPrivateKey());
            this.sshClient.authPublickey(
                    this.sshConfiguration.getUser(),
                    keys
            );
        }
    }

    public void shutdownConnection() throws IOException {
        if (this.sshClient != null && this.sshClient.isConnected()) {
            this.sshClient.disconnect();
            this.sshClient = null;
        }
    }

    public void executeCommandLogging(String command) throws IOException {
        this.executeCommand(command)
                .forEach(line -> LOG.info(">> {}", line));
    }

    public List<String> executeCommand(String command) throws IOException {
        LOG.debug("Executing command '{}'.", command);

        Session session = this.sshClient.startSession();
        Session.Command cmd = session.exec(command);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(cmd.getInputStream()))) {
            String line;
            List<String> lines = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        }
    }

    public void copyFile(String source, String target) throws IOException {
        try (SFTPClient sftpClient = this.sshClient.newSFTPClient()) {
            sftpClient.put(source, target);
        }
    }

}
