package com.deployrr.engine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.IOException;
import java.net.URI;

public class Deployrr {

    private static final Logger LOG = LogManager.getLogger(Deployrr.class);
    private static final String LOG4J_SIMPLE = "log4j2/simple.xml";
    private static final String LOG4J_VERBOSE = "log4j2/verbose.xml";

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // Arguments
        DeployrrEngineArguments arguments;
        try {
            arguments = DeployrrEngineArguments.parseArguments(args);
        } catch (InvalidEngineArgumentException e) {
            LOG.error("Invalid arguments passed.", e);
            return;
        }

        // Setup Logging
        LoggerContext logManager = (LoggerContext) LogManager.getContext(false);
        logManager.setConfigLocation(URI.create(
                arguments.isVerbose() ? LOG4J_VERBOSE : LOG4J_SIMPLE
        ));

        // Engine Deploy
        DeployrrEngine engine = new DeployrrEngine(arguments, startTime);
        try {
            engine.runDeployment();
        } catch (IOException e) {
            LOG.error(e);
            e.printStackTrace();
            System.exit(1);
        }
    }

}
