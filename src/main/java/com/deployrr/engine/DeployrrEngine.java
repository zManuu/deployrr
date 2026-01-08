package com.deployrr.engine;

import com.deployrr.configuration.DeployConfiguration;
import com.deployrr.configuration.DeployTaskConfiguration;
import com.deployrr.configuration.reader.DeployConfigurationJsonReader;
import com.deployrr.configuration.reader.DeployConfigurationReader;
import com.deployrr.configuration.reader.DeployConfigurationYamlReader;
import com.deployrr.configuration.env.DeployEnvInjector;
import com.deployrr.ssh.SSHConnection;
import com.deployrr.task.DeployTask;
import com.deployrr.task.DeployTasks;
import com.deployrr.task.TaskException;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeployrrEngine {

    private final static Logger LOG = LogManager.getLogger(DeployrrEngine.class);
    private final DeployrrEngineArguments arguments;
    private final DeployEnvInjector envInjector;
    private DeployConfiguration configuration;
    private List<DeployTask> tasks;
    private SSHConnection sshConnection;
    private DeployrrState state;

    public DeployrrEngine(DeployrrEngineArguments arguments) {
        this.arguments = arguments;
        this.state = DeployrrState.BOOT;
        this.envInjector = new DeployEnvInjector();
    }

    public void runDeployment() throws IOException, TaskException {
        this.loadConfiguration();
        this.initiateSSHConnection();
        this.prepareTasks();
        this.deploy();
        this.shutdownSSHConnection();
    }

    private void loadConfiguration() throws IOException {
        this.enterState(DeployrrState.LOAD_CONFIGURATION);
        if (this.arguments.getDeployrrFile() == null
                || !this.arguments.getDeployrrFile().exists()
                || !this.arguments.getDeployrrFile().isFile()) {
            throw new IOException("Passed configuration file does not exist or is not a file.");
        }

        // Read
        String configurationFileExt = FilenameUtils.getExtension(this.arguments.getDeployrrFile().getName());
        DeployConfigurationReader configurationReader;
        switch (configurationFileExt) {
            case "json":
                configurationReader = new DeployConfigurationJsonReader();
                break;
            case "yml":
            case "yaml":
                configurationReader = new DeployConfigurationYamlReader();
                break;
            default:
                throw new IOException("Unsupported configuration file with extension '" + configurationFileExt + "'.");
        }
        try (FileReader fileReader = new FileReader(this.arguments.getDeployrrFile())) {
            this.configuration = configurationReader.readConfiguration(fileReader);
        }

        // Inject ENV
        try {
            this.envInjector.setupEnv(this.configuration);
            this.envInjector.injectEnv(this.configuration);
        } catch (Exception e) {
            throw new IOException("Could not inject ENV.", e);
        }
    }

    private void initiateSSHConnection() throws IOException {
        this.enterState(DeployrrState.SSH_CONNECT);
        this.sshConnection = new SSHConnection(this.configuration.getSsh());
        this.sshConnection.initiateConnection();
    }

    private void prepareTasks() throws IOException {
        this.enterState(DeployrrState.PREPARE_TASKS);
        this.tasks = new ArrayList<>();
        for (DeployTaskConfiguration taskConfiguration : this.configuration.getTasks()) {
            this.tasks.add(DeployTasks.instantiateTask(this.sshConnection, taskConfiguration));
        }
    }

    private void deploy() throws TaskException {
        this.enterState(DeployrrState.DEPLOY);
        for (DeployTask task : this.tasks) {
            LOG.info("Starting Task: {}.", task.getDisplayName());
            task.execute();
        }
    }

    private void shutdownSSHConnection() throws IOException {
        this.enterState(DeployrrState.SSH_DISCONNECT);
        this.sshConnection.shutdownConnection();
        this.enterState(DeployrrState.FINISHED);
    }

    private void enterState(DeployrrState state) {
        this.state = state;
        LOG.debug("Entering deployrr state {}.", state);
    }

}
