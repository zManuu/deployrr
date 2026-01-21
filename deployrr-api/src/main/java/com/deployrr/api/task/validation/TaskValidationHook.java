package com.deployrr.api.task.validation;

public interface TaskValidationHook {

    void validate() throws TaskValidationException;

}
