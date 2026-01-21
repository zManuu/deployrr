package com.deployrr.core.engine;

import com.deployrr.api.configuration.DeployConfiguration;
import com.deployrr.api.configuration.DeployTaskConfiguration;
import com.deployrr.api.task.validation.TaskValidationHook;
import com.deployrr.core.configuration.DeployEnvInjector;
import com.deployrr.core.configuration.DeployConfigurationJsonReader;
import com.deployrr.api.configuration.DeployConfigurationReader;
import com.deployrr.core.configuration.DeployConfigurationYamlReader;
import com.deployrr.core.ssh.SSHConnectionImpl;
import com.deployrr.api.task.DeployTask;
import com.deployrr.api.task.TaskException;
import com.deployrr.api.task.TaskResult;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DeployrrEngine {

    private final static Logger LOG = LogManager.getLogger(DeployrrEngine.class);
    private final DeployrrEngineArguments arguments;
    private final DeployEnvInjector envInjector;
    private final long startTime;
    private DeployConfiguration configuration;
    private List<DeployTask> tasks;
    private SSHConnectionImpl sshConnection;
    private DeployrrState state;
    private final Map<DeployTask, List<TaskValidationHook>> validationHooks;
    private int executedValidationHooksCounter;

    public DeployrrEngine(DeployrrEngineArguments arguments, long startTime) {
        this.arguments = arguments;
        this.startTime = startTime;
        this.state = DeployrrState.BOOT;
        this.envInjector = new DeployEnvInjector();
        this.validationHooks = new LinkedHashMap<>();
    }

    public void runDeployment() throws IOException {
        if (!this.arguments.isNoBanner()) {
            DeployrrOutput.banner();
        }

        this.loadConfiguration();
        this.initiateSSHConnection();
        this.prepareTasks();
        boolean deploySuccess = this.deploy();
        boolean validationSuccess = false;
        if (deploySuccess) {
            validationSuccess = this.runValidationHooks();
        }
        this.shutdownSSHConnection();
        this.finish(deploySuccess, validationSuccess);
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
        this.sshConnection = new SSHConnectionImpl(this.configuration.getSsh());
        this.sshConnection.initiateConnection();
    }

    private void prepareTasks() throws IOException {
        this.enterState(DeployrrState.PREPARE_TASKS);
        this.tasks = new ArrayList<>();
        List<DeployTaskConfiguration> sortedTaskConfigurations = this.sortTasks(this.configuration.getTasks());
        for (DeployTaskConfiguration taskConfiguration : sortedTaskConfigurations) {
            this.tasks.add(DeployTasks.instantiateTask(this.sshConnection, taskConfiguration));
        }
    }

    public List<DeployTaskConfiguration> sortTasks(List<DeployTaskConfiguration> tasks) throws IOException {
        Map<String, DeployTaskConfiguration> byName = tasks.stream()
                .collect(Collectors.toMap(DeployTaskConfiguration::getName, t -> t));

        List<String> order = tasks.stream()
                .map(DeployTaskConfiguration::getName)
                .collect(Collectors.toList());

        for (DeployTaskConfiguration t : tasks) {
            for (String dep : t.getDepends()) {
                if (!byName.containsKey(dep)) {
                    throw new IOException("Missing dependency \"" + dep + "\" for task \"" + t.getName() + "\".");
                }
            }
        }

        boolean changed = true;
        int iterations = 0;

        while (changed) {
            changed = false;
            iterations++;

            for (int i = 0; i < order.size(); i++) {
                String name = order.get(i);
                DeployTaskConfiguration t = byName.get(name);

                for (String dep : t.getDepends()) {
                    int depIndex = order.indexOf(dep);
                    if (depIndex == -1) {
                        throw new IOException("Missing dependency during processing: " + dep);
                    }
                    if (depIndex > i) {
                        order.remove(depIndex);
                        order.add(i, dep);
                        changed = true;
                    }
                }
            }

            if (iterations > order.size() * 2) {
                throw new IOException("Cyclic dependency detected");
            }
        }

        LOG.debug("Task execution order: {}", order);
        return order.stream()
                .map(byName::get)
                .collect(Collectors.toList());
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
            List<TaskValidationHook> taskValidationHooks = task.validationHooks() != null
                    ? task.validationHooks() : new ArrayList<>();

            try {
                taskResult = task.execute();

                if (taskResult.isSuccess()) {
                    LOG.info("\u001B[32mSuccess\u001B[0m");
                } else {
                    LOG.error("\u001B[31mFailure\u001B[0m", taskResult.getException());
                    if (!task.getGeneralOptions().getIgnoreFailure()) {
                        deploySuccess = false;
                        break;
                    }
                }
            } catch (TaskException e) {
                LOG.error("\u001B[31mFailure\u001B[0m", e);
                if (!task.getGeneralOptions().getIgnoreFailure()) {
                    deploySuccess = false;
                    break;
                }
            }

            this.validationHooks.put(task, taskValidationHooks);
            DeployrrOutput.line();
        }

        return deploySuccess;
    }

    private boolean runValidationHooks() {
        this.enterState(DeployrrState.VALIDATION);
        LOG.info("Running validation hooks.");
        boolean validationSuccess = true;

        for (Map.Entry<DeployTask, List<TaskValidationHook>> deployTaskListEntry : this.validationHooks.entrySet()) {
            DeployTask task = deployTaskListEntry.getKey();
            List<TaskValidationHook> taskValidationHooks = deployTaskListEntry.getValue();

            if (task.getGeneralOptions().getIgnoreValidationHooks()
                    || taskValidationHooks == null
                    || taskValidationHooks.isEmpty()) {
                continue;
            }

            boolean taskValidationSuccess = true;
            DeployrrOutput.line(String.format("%s: %s", task.getDisplayName(), taskValidationHooks.size()));
            for (TaskValidationHook taskValidationHook : taskValidationHooks) {
                try {
                    taskValidationHook.validate();
                } catch (Exception e) {
                    LOG.error("\u001B[31mValidation Failure\u001B[0m", e);
                    validationSuccess = false;
                    taskValidationSuccess = false;
                    break;
                }
                this.executedValidationHooksCounter += 1;
            }
            DeployrrOutput.line();

            if (!taskValidationSuccess) {
                break;
            }
        }

        return validationSuccess;
    }

    private void shutdownSSHConnection() throws IOException {
        this.enterState(DeployrrState.SSH_DISCONNECT);
        this.sshConnection.shutdownConnection();
    }

    private void finish(boolean deploySuccess, boolean validationSuccess) {
        if (deploySuccess && validationSuccess) {
            LOG.info("\u001B[1m\u001B[32mDEPLOYMENT SUCCESS\u001B[0m");
            DeployrrOutput.line("Stats");
            LOG.info("Executed tasks: {}", this.tasks.size());
            LOG.info("Executed validation hooks: {}", this.executedValidationHooksCounter);
            LOG.info("Total time: {} ms", System.currentTimeMillis() - this.startTime);
        } else if (!deploySuccess) {
            DeployrrOutput.line();
            LOG.info("\u001B[1m\u001B[31mDEPLOYMENT FAILURE\u001B[0m");
        } else {
            DeployrrOutput.line();
            LOG.info("\u001B[1m\u001B[31mDEPLOYMENT VALIDATION FAILURE\u001B[0m");
        }
        DeployrrOutput.line();
        this.enterState(DeployrrState.FINISHED);
    }

    private void enterState(DeployrrState state) {
        this.state = state;
        LOG.debug("Entering deployrr state {}.", state);
    }

}
