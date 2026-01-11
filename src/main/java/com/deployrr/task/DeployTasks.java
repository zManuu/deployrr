package com.deployrr.task;

import com.deployrr.configuration.DeployTaskConfiguration;
import com.deployrr.ssh.SSHConnection;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DeployTasks {

    private interface Transformer {
        Object transform(String value) throws Exception;
    }

    private static final Reflections REF = new Reflections(
            "com.deployrr",
            Scanners.TypesAnnotated
    );

    private DeployTasks() {
    }

    public static DeployTask instantiateTask(SSHConnection sshConnection, DeployTaskConfiguration taskConfiguration) throws IOException {
        String taskName = taskConfiguration.getTask();
        Class<? extends DeployTask> taskClass = findTaskClass(taskName);
        if (taskClass == null) {
            throw new IOException("Unsupported task '" + taskName + "'.");
        }

        DeployTask task;
        try {
            task = instantiateTask(taskClass, sshConnection, taskConfiguration.getName());
        } catch (Exception e) {
            throw new IOException("Could not instantiate task '" + taskName + "'.", e);
        }
        try {
            injectTaskOpt(task, taskConfiguration.getOpt());
        } catch (Exception e) {
            throw new IOException("Could not inject options for task '" + taskName + "'.", e);
        }
        return task;
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends DeployTask> findTaskClass(String command) {
        Set<Class<?>> taskClasses = REF.getTypesAnnotatedWith(Task.class);
        for (Class<?> taskClass : taskClasses) {
            Task taskData = taskClass.getAnnotation(Task.class);
            String[] taskKeys = taskData.keys();
            for (String taskKey : taskKeys) {
                if (!Objects.equals(taskKey.toLowerCase(), command.toLowerCase())) {
                    continue;
                }
                if (!DeployTask.class.isAssignableFrom(taskClass)) {
                    continue;
                }
                return (Class<? extends DeployTask>) taskClass;
            }
        }
        return null;
    }

    private static DeployTask instantiateTask(Class<? extends DeployTask> taskClass, SSHConnection sshConnection, String name) throws Exception {
        Constructor<? extends DeployTask> constructor = taskClass.getConstructor(SSHConnection.class, String.class);
        return constructor.newInstance(sshConnection, name);
    }

    private static void injectTaskOpt(DeployTask task, Map<String, String> opt) throws Exception {
        Class<? extends DeployTask> taskClass = task.getClass();
        for (Field field : taskClass.getDeclaredFields()) {
            if (field.isSynthetic()) {
                continue;
            }
            if (!field.isAnnotationPresent(TaskOpt.class)) {
                continue;
            }
            TaskOpt optData = field.getAnnotation(TaskOpt.class);
            if (opt == null) {
                if (optData.required()) {
                    throw new IOException("Missing the required option '" + optData.value() + "'.");
                } else {
                    continue;
                }
            }
            String optValue = opt.get(optData.value());
            if (optValue == null && optData.required()) {
                throw new IOException("Missing the required option '" + optData.value() + "'.");
            } else if (optValue == null) {
                continue;
            }

            field.setAccessible(true);
            Object transformed = transformTaskOpt(optValue, field.getType());
            field.set(task, transformed);
        }
    }

    private static Object transformTaskOpt(String taskOpt, Class<?> expectedType) throws IOException {
        Map<Class<?>, Transformer> transformers = Map.of(
                String.class, (s) -> s,
                Boolean.class, Boolean::parseBoolean,
                Integer.class, Integer::parseInt,
                Double.class, Double::parseDouble,
                Float.class, Float::parseFloat,
                Long.class, Long::parseLong
        );
        Transformer transformer = transformers.get(expectedType);
        if (transformer == null) {
            throw new IOException("No transformer was found for type '" + expectedType.getName() + "'.");
        }
        try {
            return transformer.transform(taskOpt);
        } catch (Exception e) {
            throw new IOException("Error whilst executing transformer (type '" + expectedType.getName() + "').", e);
        }
    }

}
