package com.deployrr.engine;

import com.deployrr.task.TaskException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Deployrr {

    private static final Logger LOG = LogManager.getLogger(Deployrr.class);

    public static void main(String[] args) {

        // Arguments
        DeployrrEngineArguments arguments;
        try {
            arguments = DeployrrEngineArguments.parseArguments(args);
        } catch (InvalidEngineArgumentException e) {
            LOG.error("Invalid arguments passed.", e);
            return;
        }
        LOG.info("Using deployrr args: {}", arguments);

        // Engine Deploy
        DeployrrEngine engine = new DeployrrEngine(arguments);
        try {
            engine.runDeployment();
        } catch (IOException | TaskException e) {
            LOG.error(e);
            e.printStackTrace();
            System.exit(1);
        }
    }

}
