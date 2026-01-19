package com.deployrr.api.plugin;

import com.deployrr.api.task.DeployTask;

import java.util.Set;

public interface DeployrrPlugin {

    void start();
    Set<Class<? extends DeployTask>> taskClasses();

}
