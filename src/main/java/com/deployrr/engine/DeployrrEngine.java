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
import com.deployrr.task.TaskResult;
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
    private final long startTime;
    private DeployConfiguration configuration;
    private List<DeployTask> tasks;
    private SSHConnection sshConnection;
    private DeployrrState state;

    public DeployrrEngine(DeployrrEngineArguments arguments, long startTime) {
        this.arguments = arguments;
        this.startTime = startTime;
        this.state = DeployrrState.BOOT;
        this.envInjector = new DeployEnvInjector();
    }

    public void runDeployment() throws IOException {
        if (!this.arguments.isNoBanner()) {
            DeployrrOutput.banner();
        }

        this.loadConfiguration();
        this.initiateSSHConnection();
        this.prepareTasks();
        boolean deploySuccess = this.deploy();
        this.shutdownSSHConnection();
        this.finish(deploySuccess);
    }

    private void loadConfiguration() throws IOException {
        this.enterState(DeployrrState.LOAD_CONFIGURATION);
        LOG.info("Loading configuration.");
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
        LOG.info("Initiating SSH connection.");
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

    private boolean deploy() {
        this.enterState(DeployrrState.DEPLOY);
        LOG.info("Running deployment.");
        boolean deploySuccess = true;

        // Execute tasks
        for (int i = 0; i < this.tasks.size(); i++) {
            DeployTask task = this.tasks.get(i);
            DeployrrOutput.line(String.format("%s (%s/%s)", task.getDisplayName(), i+1, this.tasks.size()));
            TaskResult taskResult;
            try {
                taskResult = task.execute();
            } catch (TaskException e) {
                LOG.error("Error whilst executing task.", e);
                deploySuccess = false;
                break;
            }
            if (taskResult.isSuccess()) {
                LOG.info("\u001B[32mSuccess\u001B[0m");
            } else {
                LOG.error("Error whilst executing task.", taskResult.getException());
                deploySuccess = false;
                break;
            }
            DeployrrOutput.line();
        }

        return deploySuccess;
    }

    private void shutdownSSHConnection() throws IOException {
        this.enterState(DeployrrState.SSH_DISCONNECT);
        this.sshConnection.shutdownConnection();
    }

    private void finish(boolean deploySuccess) {
        if (deploySuccess) {
            LOG.info("\u001B[1m\u001B[32mDEPLOYMENT SUCCESS\u001B[0m");
            DeployrrOutput.line("Stats");
            LOG.info("Executed tasks: {}", this.tasks.size());
            LOG.info("Total time: {} ms", System.currentTimeMillis() - this.startTime);
        } else {
            DeployrrOutput.line();
            LOG.info("\u001B[1m\u001B[31mDEPLOYMENT FAILURE\u001B[0m");
        }
        DeployrrOutput.line();
        this.enterState(DeployrrState.FINISHED);
    }

    private void enterState(DeployrrState state) {
        this.state = state;
        LOG.debug("Entering deployrr state {}.", state);
    }

}
