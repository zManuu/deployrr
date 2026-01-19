package com.deployrr.pluginexample;

import com.deployrr.api.plugin.DeployrrPlugin;
import com.deployrr.api.task.DeployTask;

import java.util.Set;

public class ExamplePlugin implements DeployrrPlugin {

    @Override
    public void start() {
        System.out.println("Hello world! (plugin start)");
    }

    @Override
    public Set<Class<? extends DeployTask>> taskClasses() {
        return Set.of(CustomTask.class);
    }

}