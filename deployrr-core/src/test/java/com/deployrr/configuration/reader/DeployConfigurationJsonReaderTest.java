package com.deployrr.configuration.reader;

import com.deployrr.api.configuration.DeployConfiguration;
import com.deployrr.api.configuration.DeployTaskConfiguration;
import com.deployrr.core.configuration.DeployConfigurationJsonReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class DeployConfigurationJsonReaderTest {

    private final DeployConfigurationJsonReader jsonReader = new DeployConfigurationJsonReader();

    @Test
    public void testReadSimple() throws IOException {
        try (
                InputStream inputStream = this.getClass().getResourceAsStream("/configuration/simple/deployrr.json");
                InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(inputStream))
        ) {
            DeployConfiguration configuration = this.jsonReader.readConfiguration(inputStreamReader);
            assertEquals("1.0.0-ALPHA", configuration.getDeployrrVersion());
            assertNotNull(configuration.getSsh());
            assertEquals("TEST", configuration.getSsh().getHost());
            assertEquals("root", configuration.getSsh().getUser());
            assertNull(configuration.getSsh().getPassword());
            assertEquals("/home/manuel/.ssh/id_rsa", configuration.getSsh().getPrivateKey());
            assertNull(configuration.getVariables());
            assertNotNull(configuration.getTasks());
            assertInstanceOf(DeployTaskConfiguration.class, configuration.getTasks().get(0));
        }
    }

}
