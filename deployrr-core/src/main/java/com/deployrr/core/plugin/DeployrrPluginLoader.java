package com.deployrr.core.plugin;

import com.deployrr.api.plugin.DeployrrPlugin;
import com.deployrr.core.engine.DeployTasks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DeployrrPluginLoader {

    private final static Logger LOG = LogManager.getLogger(DeployrrPluginLoader.class);
    private final Path pluginsDirectory;
    private final Set<DeployrrPlugin> plugins;
    private URLClassLoader pluginClassLoader;

    public DeployrrPluginLoader(Path pluginsDirectory) {
        this.pluginsDirectory = pluginsDirectory;
        this.plugins = new HashSet<>();
    }

    public void loadPlugins() {
        this.plugins.clear();

        File pluginsDirectoryFile = this.pluginsDirectory.toFile();
        if (!pluginsDirectoryFile.exists() || !pluginsDirectoryFile.isDirectory()) {
            return;
        }

        List<URL> pluginUrls = new ArrayList<>();
        try (DirectoryStream<Path> pluginPaths = Files.newDirectoryStream(this.pluginsDirectory)) {
            for (Path pluginPath : pluginPaths) {
                pluginUrls.add(pluginPath.toUri().toURL());
            }
        } catch (IOException e) {
            LOG.error("Could not load plugins.", e);
        }

        this.pluginClassLoader = new URLClassLoader(
                pluginUrls.toArray(URL[]::new),
                this.getClass().getClassLoader()
        );
        ServiceLoader<DeployrrPlugin> pluginServiceLoader = ServiceLoader.load(
                DeployrrPlugin.class,
                this.pluginClassLoader
        );
        for (DeployrrPlugin plugin : pluginServiceLoader) {
            plugin.taskClasses().forEach(DeployTasks::addAdditionalTaskClass);
            this.plugins.add(plugin);
            plugin.start();
        }
    }

    public void shutdown() {
        try {
            this.pluginClassLoader.close();
        } catch (IOException e) {
            LOG.error("Could not close plugin class-loader.", e);
        }
    }

}
