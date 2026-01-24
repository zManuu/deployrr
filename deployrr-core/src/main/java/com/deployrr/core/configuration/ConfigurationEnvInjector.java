package com.deployrr.core.configuration;

import com.deployrr.api.configuration.DeployConfiguration;
import com.deployrr.api.configuration.env.EnvInject;
import com.deployrr.api.configuration.env.EnvInjectType;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigurationEnvInjector {

    private final Map<String, String> env = new HashMap<>();

    public void setupEnv(DeployConfiguration deployConfiguration) {
        this.setupEnv(deployConfiguration, "./");
    }

    public void setupEnv(DeployConfiguration deployConfiguration, String envDirectory) {
        if (deployConfiguration != null && deployConfiguration.getVariables() != null) {
            deployConfiguration.getVariables().forEach(this.env::putIfAbsent);
        }

        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .directory(envDirectory)
                .load();
        for (DotenvEntry dotenvEntry : dotenv.entries()) {
            this.env.putIfAbsent(dotenvEntry.getKey(), dotenvEntry.getValue());
        }
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
                case STRING_LIST:
                    List<String> stringList = (List<String>) fieldValue;
                    stringList.replaceAll(this::injectEnv);
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
