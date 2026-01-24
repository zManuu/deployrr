package com.deployrr.core.engine;

import com.deployrr.api.configuration.DeployTaskConfiguration;
import com.deployrr.api.ssh.SSHConnection;
import com.deployrr.api.task.*;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashSet;
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
    private static final Set<Class<? extends DeployTask>> ADDITIONAL_TASK_CLASSES = new HashSet<>();

    private DeployTasks() {
    }

    public static void addAdditionalTaskClass(Class<? extends DeployTask> taskClass) {
        ADDITIONAL_TASK_CLASSES.add(taskClass);
    }

    public static DeployTask instantiateTask(SSHConnection sshConnection, DeployTaskConfiguration taskConfiguration) throws IOException {
        String taskName = taskConfiguration.getTask();
        Class<? extends DeployTask> taskClass = findTaskClass(taskName);
        if (taskClass == null) {
            throw new IOException("Unsupported task '" + taskName + "'.");
        }

        // General options
        DeployTaskGeneralOptions generalOptions = new DeployTaskGeneralOptions();
        try {
            injectTaskOpt(generalOptions, taskConfiguration.getOpt());
        } catch (Exception e) {
            throw new IOException("Could not inject options into general-options object.", e);
        }
        DeployTaskParameters taskParameters = new DeployTaskParameters(sshConnection, taskConfiguration.getName(), generalOptions);

        DeployTask task;
        try {
            task = instantiateTask(taskClass, taskParameters);
        } catch (Exception e) {
            throw new IOException("Could not instantiate task '" + taskName + "'.", e);
        }
        try {
            injectTaskOpt(task, taskConfiguration.getOpt());
        } catch (Exception e) {
            throw new IOException("Could not inject options for task '" + task.getDisplayName() + "'.", e);
        }
        return task;
    }

    public static Set<Class<?>> findTaskClasses() {
        Set<Class<?>> classes = REF.getTypesAnnotatedWith(Task.class);
        classes.addAll(ADDITIONAL_TASK_CLASSES);
        return classes;
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends DeployTask> findTaskClass(String command) {
        Set<Class<?>> taskClasses = findTaskClasses();
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

    private static DeployTask instantiateTask(Class<? extends DeployTask> taskClass, DeployTaskParameters taskParameters) throws Exception {
        Constructor<? extends DeployTask> constructor = taskClass.getConstructor(DeployTaskParameters.class);
        return constructor.newInstance(taskParameters);
    }

    private static void injectTaskOpt(Object injectable, Map<String, String> opt) throws Exception {
        Class<?> clazz = injectable.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isSynthetic() || !field.isAnnotationPresent(TaskOpt.class)) {
                continue;
            }

            TaskOpt optData = field.getAnnotation(TaskOpt.class);
            String key = optData.value();

            // compute value
            String rawValue = (opt != null) ? opt.get(key) : null;
            String finalValue = resolveValue(rawValue, optData);
            if (finalValue == null) {
                continue;
            }

            // transform & set
            Object transformed = transformTaskOpt(finalValue, field.getType());
            field.setAccessible(true);
            field.set(injectable, transformed);
        }
    }

    private static String resolveValue(String rawValue, TaskOpt optData) throws IOException {
        if (rawValue != null && !rawValue.isEmpty()) {
            return rawValue;
        }
        String defaultValue = optData.defaultValue();
        boolean hasDefault = defaultValue != null && !defaultValue.isEmpty() && !defaultValue.equals(Task.NULL);
        if (hasDefault) {
            return defaultValue;
        }
        if (optData.required()) {
            throw new IOException("Missing the required option '" + optData.value() + "'.");
        }
        return null;
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
