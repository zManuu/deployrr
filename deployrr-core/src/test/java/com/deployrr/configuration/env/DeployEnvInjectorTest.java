package com.deployrr.configuration.env;

import com.deployrr.api.configuration.DeployConfiguration;
import com.deployrr.api.configuration.DeploySSHConfiguration;
import com.deployrr.api.configuration.DeployTaskConfiguration;
import com.deployrr.core.configuration.DeployEnvInjector;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DeployEnvInjectorTest {

    @Test
    public void testInjectionWithEnvFile() throws Exception {
        DeployConfiguration deployConfiguration = new DeployConfiguration(
                null,
                new DeploySSHConfiguration(
                        "${SRV_HOST}",
                        "${SRV_USER}",
                        null,
                        null
                ),
                Collections.singletonList(new DeployTaskConfiguration(
                        "docker_compose_up",
                        new HashMap<>(Map.of("location", "${SRV_DIR}/docker-compose.yml"))
                )),
                null
        );
        DeployEnvInjector envInjector = new DeployEnvInjector();
        envInjector.setupEnv(null, "/configuration/env/");
        envInjector.injectEnv(deployConfiguration);

        assertNotNull(deployConfiguration.getSsh());
        assertEquals("TEST", deployConfiguration.getSsh().getHost());
        assertEquals("root", deployConfiguration.getSsh().getUser());
        assertNotNull(deployConfiguration.getTasks());
        DeployTaskConfiguration task = deployConfiguration.getTasks().get(0);
        assertNotNull(task);
        assertNotNull(task.getOpt());
        assertEquals("/test/docker-compose.yml", task.getOpt().get("location"));
    }

    @Test
    public void testInjectionWithVariables() throws Exception {
        DeployConfiguration deployConfiguration = new DeployConfiguration(
                null,
                new DeploySSHConfiguration(
                        "${SRV_HOST}",
                        "${SRV_USER}",
                        null,
                        null
                ),
                Collections.singletonList(new DeployTaskConfiguration(
                        "docker_compose_up",
                        new HashMap<>(Map.of("location", "${SRV_DIR}/docker-compose.yml"))
                )),
                Map.of("SRV_HOST", "TEST", "SRV_USER", "root", "SRV_DIR", "/test")
        );
        DeployEnvInjector envInjector = new DeployEnvInjector();
        envInjector.setupEnv(deployConfiguration);
        envInjector.injectEnv(deployConfiguration);

        assertNotNull(deployConfiguration.getSsh());
        assertEquals("TEST", deployConfiguration.getSsh().getHost());
        assertEquals("root", deployConfiguration.getSsh().getUser());
        assertNotNull(deployConfiguration.getTasks());
        DeployTaskConfiguration task = deployConfiguration.getTasks().get(0);
        assertNotNull(task);
        assertNotNull(task.getOpt());
        assertEquals("/test/docker-compose.yml", task.getOpt().get("location"));
    }

}
