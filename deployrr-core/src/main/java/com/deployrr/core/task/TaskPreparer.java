package com.deployrr.core.task;

import com.deployrr.api.configuration.DeployTaskConfiguration;
import com.deployrr.api.ssh.SSHConnection;
import com.deployrr.api.task.DeployTask;
import com.deployrr.core.engine.DeployTasks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Responsible for instantiating {@link DeployTask}s and ordering them in the according execution order.
 */
public class TaskPreparer {

    private final static Logger LOG = LogManager.getLogger(TaskPreparer.class);

    private final List<DeployTaskConfiguration> tasks;
    private final SSHConnection sshConnection;

    public TaskPreparer(List<DeployTaskConfiguration> tasks, SSHConnection sshConnection) {
        this.tasks = tasks;
        this.sshConnection = sshConnection;
    }

    public List<DeployTask> prepareTasks() throws IOException {
        List<DeployTask> deployTasks = new ArrayList<>();
        List<DeployTaskConfiguration> sortedTaskConfigurations = this.sortTasks(this.tasks);
        for (DeployTaskConfiguration taskConfiguration : sortedTaskConfigurations) {
            deployTasks.add(DeployTasks.instantiateTask(this.sshConnection, taskConfiguration));
        }
        return deployTasks;
    }

    private List<DeployTaskConfiguration> sortTasks(List<DeployTaskConfiguration> tasks) throws IOException {
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

}
