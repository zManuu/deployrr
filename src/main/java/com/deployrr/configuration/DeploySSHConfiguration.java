package com.deployrr.configuration;

public class DeploySSHConfiguration {

    private String host;
    private String user;
    private String password;
    private String publicKey;

    public DeploySSHConfiguration(String host, String user, String password, String publicKey) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.publicKey = publicKey;
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

    public String getPublicKey() {
        return publicKey;
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

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
