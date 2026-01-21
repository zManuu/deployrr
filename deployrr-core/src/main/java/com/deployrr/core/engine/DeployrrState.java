package com.deployrr.core.engine;

public enum DeployrrState {

    BOOT,
    LOAD_CONFIGURATION,
    SSH_CONNECT,
    PREPARE_TASKS,
    DEPLOY,
    VALIDATION,
    SSH_DISCONNECT,
    FINISHED

}
