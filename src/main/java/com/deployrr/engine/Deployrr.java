package com.deployrr.engine;

import com.deployrr.task.TaskException;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Deployrr {

    private static final String OPT_DEPLOYRR_FILE = "deployrr-file";
    private static final String OPT_DEPLOYRR_FILE_DEFAULT = "./deployrr.json";
    private static final Logger LOG = LogManager.getLogger();

    public static void main(String[] args) {
        Map<String, String> arguments = parseArguments(args);
        LOG.info("Using deployrr args: {}", arguments);

        File configurationFile = new File(arguments.get(OPT_DEPLOYRR_FILE));
        DeployrrEngine engine = new DeployrrEngine(configurationFile);
        try {
            engine.runDeployment();
        } catch (IOException | TaskException e) {
            LOG.error(e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    protected static Map<String, String> parseArguments(String[] args) {
        Map<String, String> parsedArguments = new HashMap<>();
        LongOpt[] opts = new LongOpt[] {
                new LongOpt("deployrr-file", LongOpt.OPTIONAL_ARGUMENT, null, 'f')
        };
        Getopt g = new Getopt("Deployrr", args, "f:", opts);
        for (int c = g.getopt(); c != -1; c = g.getopt()) {
            switch (c) {
                case 'f':
                    parsedArguments.put(OPT_DEPLOYRR_FILE, g.getOptarg());
                    break;
                default:
                    break;
            }
        }

        // apply defaults
        if (!parsedArguments.containsKey(OPT_DEPLOYRR_FILE)) {
            parsedArguments.put(OPT_DEPLOYRR_FILE, OPT_DEPLOYRR_FILE_DEFAULT);
        }

        return parsedArguments;
    }

}
