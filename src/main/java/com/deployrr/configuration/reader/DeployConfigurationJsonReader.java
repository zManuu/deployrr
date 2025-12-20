package com.deployrr.configuration.reader;

import com.deployrr.configuration.DeployConfiguration;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;

public class DeployConfigurationJsonReader implements DeployConfigurationReader {

    private final Gson gson = new Gson();

    @Override
    public DeployConfiguration readConfiguration(Reader reader) throws IOException {
        try {
            return this.gson.fromJson(reader, DeployConfiguration.class);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

}
