package com.deployrr.api.configuration;

import java.io.IOException;
import java.io.Reader;

public interface ConfigurationReader {

    DeployConfiguration readConfiguration(Reader reader) throws IOException;

}
