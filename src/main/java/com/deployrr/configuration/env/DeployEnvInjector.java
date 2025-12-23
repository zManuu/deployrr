package com.deployrr.configuration.env;

import com.deployrr.configuration.DeployConfiguration;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeployEnvInjector {

    private Map<String, String> env;

    public void setupEnv(DeployConfiguration deployConfiguration) {
        this.setupEnv(deployConfiguration, "./");
    }

    public void setupEnv(DeployConfiguration deployConfiguration, String envDirectory) {
        Map<String, String> env = new HashMap<>();
        if (deployConfiguration != null && deployConfiguration.getVariables() != null) {
            deployConfiguration.getVariables().forEach(env::putIfAbsent);
        }

        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .directory(envDirectory)
                .load();
        for (DotenvEntry dotenvEntry : dotenv.entries()) {
            env.putIfAbsent(dotenvEntry.getKey(), dotenvEntry.getValue());
        }
        this.env = env;
    }

    public void injectEnv(Object object) throws Exception {
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isSynthetic()) {
                continue;
            }
            if (!field.isAnnotationPresent(EnvInject.class)) {
                continue;
            }

            field.setAccessible(true);
            Object fieldValue = field.get(object);

            if (fieldValue == null) {
                continue;
            }

            EnvInjectType objectType = field.getAnnotation(EnvInject.class).value();
            switch (objectType) {
                case STRING:
                    field.set(object, this.injectEnv((String) fieldValue));
                    break;
                case OBJECT_LIST:
                    List<?> list = (List<?>) fieldValue;
                    for (Object listEntry : list) {
                        this.injectEnv(listEntry);
                    }
                    break;
                case STRING_MAP:
                    Map<String, String> map = (Map<String, String>) fieldValue;
                    map.replaceAll((k, v) -> this.injectEnv(map.get(k)));
                    break;
                case OBJECT:
                    this.injectEnv(fieldValue);
                    break;
            }
        }
    }

    private String injectEnv(String string) {
        if (string == null) {
            return null;
        }

        for (String key : this.env.keySet()) {
            String value = this.env.get(key);
            string = string.replaceAll(
                    String.format("\\$\\{%s\\}", key),
                    value
            );
        }
        return string;
    }

}
