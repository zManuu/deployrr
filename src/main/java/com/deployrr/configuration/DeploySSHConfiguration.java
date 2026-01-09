package com.deployrr.configuration;

import com.deployrr.configuration.env.EnvInject;
import com.deployrr.configuration.env.EnvInjectType;

public class DeploySSHConfiguration {

    @EnvInject(EnvInjectType.STRING)
    private String host;

    @EnvInject(EnvInjectType.STRING)
    private String user;

    @EnvInject(EnvInjectType.STRING)
    private String password;

    @EnvInject(EnvInjectType.STRING)
    private String privateKey;

    public DeploySSHConfiguration(String host, String user, String password, String privateKey) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.privateKey = privateKey;
    }

    public DeploySSHConfiguration() {
    }

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
