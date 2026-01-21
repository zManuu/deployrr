package com.deployrr.api.task;

public class DeployTaskGeneralOptions {

    @TaskOpt(value = "general_test", required = false, example = "Ciao", defaultValue = "Moin")
    private String test;

}
