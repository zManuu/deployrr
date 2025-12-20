package com.deployrr.configuration.reader;

import com.deployrr.configuration.DeployConfiguration;

import java.io.IOException;
import java.io.Reader;

public interface DeployConfigurationReader {

    DeployConfiguration readConfiguration(Reader reader) throws IOException;

}
