package com.deployrr.core.engine.arguments;

import java.io.File;

public class EngineArguments {

    private File deployrrFile;
    private boolean verbose;
    private boolean noBanner;

    public void setDeployrrFile(File deployrrFile) {
        this.deployrrFile = deployrrFile;
    }

    public File getDeployrrFile() {
        return deployrrFile;
    }

    public void setVerbose() {
        this.verbose = true;
    }

    public void setNoBanner() {
        this.noBanner = true;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public boolean isNoBanner() {
        return noBanner;
    }

    @Override
    public String toString() {
        return "DeployrrEngineArguments{" +
                "deployrrFile=" + deployrrFile +
                ", verbose=" + verbose +
                ", noBanner=" + noBanner +
                '}';
    }

}
