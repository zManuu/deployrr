package com.deployrr.engine;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;

public class DeployrrEngineArguments {

    private static final String OPT_DEPLOYRR_FILE = "deployrr-file";
    private static final Pattern OPT_DEPLOYRR_FILE_REGEX = Pattern.compile("(?i)^deployrr\\.(json|ya?ml)$");

    private File deployrrFile;

    public static DeployrrEngineArguments parseArguments(String[] args) throws InvalidEngineArgumentException {
        DeployrrEngineArguments arguments = new DeployrrEngineArguments();
        LongOpt[] opts = new LongOpt[] {
                new LongOpt(OPT_DEPLOYRR_FILE, LongOpt.OPTIONAL_ARGUMENT, null, 'f')
        };
        Getopt g = new Getopt("Deployrr", args, "f:", opts);
        for (int c = g.getopt(); c != -1; c = g.getopt()) {
            switch (c) {
                case 'f':
                    arguments.setDeployrrFile(new File(g.getOptarg()));
                    break;
                default:
                    break;
            }
        }

        // apply defaults
        if (arguments.deployrrFile == null) {
            arguments.setDeployrrFile(findDeployrrFile());
        }

        return arguments;
    }

    private static File findDeployrrFile() throws InvalidEngineArgumentException {
        File cwd = new File(System.getProperty("user.dir"));
        File[] matches = cwd.listFiles(file ->
                file.isFile() && OPT_DEPLOYRR_FILE_REGEX.matcher(file.getName()).matches()
        );

        if (matches == null || matches.length == 0) {
            throw new InvalidEngineArgumentException("No deployrr file could be found. Please create a file named " +
                    "'deployrr.json' or 'deployrr.yaml' in the working directory or pass a file path via the -f argument.");
        }
        if (matches.length > 1) {
            throw new InvalidEngineArgumentException("Multiple deployrr files were found in the working directory: "
                    + Arrays.toString(Arrays.stream(matches).map(File::getPath).toArray()));
        }
        return matches[0];
    }

    public void setDeployrrFile(File deployrrFile) {
        this.deployrrFile = deployrrFile;
    }

    public File getDeployrrFile() {
        return deployrrFile;
    }

    @Override
    public String toString() {
        return "DeployrrEngineArguments{" +
                "deployrrFile=" + deployrrFile +
                '}';
    }

}
