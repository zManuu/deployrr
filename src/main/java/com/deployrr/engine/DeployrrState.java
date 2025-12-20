package com.deployrr.engine;

public enum DeployrrState {

    BOOT,
    LOAD_CONFIGURATION,
    SSH_CONNECT,
    PREPARE_TASKS,
    DEPLOY,
    SSH_DISCONNECT,
    FINISHED

}
