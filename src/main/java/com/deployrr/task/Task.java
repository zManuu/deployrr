package com.deployrr.task;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Task {

    String NULL = "###NULL###";

    String name();
    String[] keys() default {};
    String description() default NULL;

}
