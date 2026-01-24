package com.deployrr.core.engine;

import com.deployrr.api.configuration.DeployConfiguration;
import com.deployrr.api.ssh.SSHConnection;
import com.deployrr.api.task.validation.TaskValidationHook;
import com.deployrr.core.configuration.ConfigurationLoader;
import com.deployrr.core.configuration.DeployEnvInjector;
import com.deployrr.core.engine.arguments.EngineArguments;
import com.deployrr.core.engine.executor.DeploymentExecutor;
import com.deployrr.core.engine.executor.ValidationExecutor;
import com.deployrr.api.task.DeployTask;
import com.deployrr.core.ssh.SSHConnector;
import com.deployrr.core.task.TaskPreparer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class DeployrrEngine {

    private final static Logger LOG = LogManager.getLogger(DeployrrEngine.class);
    public static final String DEPLOY_SUCCESS = "\u001B[1m\u001B[32mDEPLOYMENT SUCCESS\u001B[0m";
    public static final String DEPLOY_FAILURE = "\u001B[1m\u001B[31mDEPLOYMENT FAILURE\u001B[0m";
    public static final String DEPLOY_VALIDATION_FAILURE = "\u001B[1m\u001B[31mDEPLOYMENT VALIDATION FAILURE\u001B[0m";

    private final EngineArguments arguments;
    private final long startTime;
    private List<DeployTask> tasks;

    public DeployrrEngine(EngineArguments arguments, long startTime) {
        this.arguments = arguments;
        this.startTime = startTime;
    }

    public void runDeployment() throws IOException {
        if (!this.arguments.isNoBanner()) {
            DeployrrOutput.banner();
        }

        ConfigurationLoader configurationLoader = new ConfigurationLoader(new DeployEnvInjector(), this.arguments.getDeployrrFile());
        DeployConfiguration configuration = configurationLoader.loadConfiguration();

        SSHConnector sshConnector = new SSHConnector(configuration.getSsh());
        SSHConnection sshConnection = sshConnector.establishSSHConnection();

        TaskPreparer taskPreparer = new TaskPreparer(configuration.getTasks(), sshConnection);
        this.tasks = taskPreparer.prepareTasks();

        Map<DeployTask, List<TaskValidationHook>> validationHooks = new LinkedHashMap<>();
        DeploymentExecutor deploymentExecutor = new DeploymentExecutor(this.tasks, validationHooks);
        boolean deploySuccess = deploymentExecutor.deploy();

        ValidationExecutor validationExecutor = new ValidationExecutor(validationHooks);
        boolean validationSuccess = false;
        if (deploySuccess) {
            validationSuccess = validationExecutor.runValidationHooks();
        }

        sshConnection.shutdownConnection();
        this.finish(deploySuccess, validationSuccess);
    }

    private void finish(boolean deploySuccess, boolean validationSuccess) {
        if (deploySuccess && validationSuccess) {
            LOG.info(DEPLOY_SUCCESS);
            DeployrrOutput.line("Stats");
            LOG.info("Executed tasks: {}", this.tasks.size());
            LOG.info("Total time: {} ms", System.currentTimeMillis() - this.startTime);
        } else if (!deploySuccess) {
            DeployrrOutput.line();
            LOG.info(DEPLOY_FAILURE);
        } else {
            DeployrrOutput.line();
            LOG.info(DEPLOY_VALIDATION_FAILURE);
        }
        DeployrrOutput.line();
    }

}
