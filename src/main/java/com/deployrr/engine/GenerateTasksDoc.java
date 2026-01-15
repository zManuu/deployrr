package com.deployrr.engine;

import com.deployrr.task.DeployTasks;
import com.deployrr.task.Task;
import com.deployrr.task.TaskOpt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class GenerateTasksDoc {

    private static final String BR = "\n";
    private static final String BR_2 = "\n\n";
    private static final String H_2 = "## ";
    private static final String H_3 = "### ";
    private static final String BOLD = "**";
    private static final String TABLE_SEPARATOR = " | ";
    private static final String OPTIONS_TABLE_HEADER = "| Option | Type | Required |\n|---|---|---|\n";

    public static void main(String[] args) {
        Set<Class<?>> taskClasses = DeployTasks.findTaskClasses();
        StringBuilder sb = new StringBuilder()
                .append(H_2)
                .append("Tasks")
                .append(BR_2);

        for (Class<?> taskClass : taskClasses) {
            if (!taskClass.isAnnotationPresent(Task.class)) {
                System.out.printf("TaskClass %s is missing Task annotation!%n", taskClass.getName());
                continue;
            }

            appendTask(taskClass, sb);
        }

        // write file
        String fileContent = sb.toString();
        File taskDocFile = new File("docs/content/Tasks.md");
        try (FileWriter fileWriter = new FileWriter(taskDocFile)) {
            fileWriter.write(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void appendTask(Class<?> taskClass, StringBuilder sb) {
        Task task = taskClass.getAnnotation(Task.class);
        Set<Field> taskOptFields = new HashSet<>();
        Field[] taskClassFields = taskClass.getDeclaredFields();
        for (Field taskClassField : taskClassFields) {
            if (taskClassField.isSynthetic() || !taskClassField.isAnnotationPresent(TaskOpt.class)) {
                continue;
            }
            taskOptFields.add(taskClassField);
        }

        sb.append(H_3)
                .append(task.name())
                .append(BR_2);

        if (!Objects.equals(task.description(), Task.NULL)) {
            sb.append(task.description())
                    .append(BR_2);
        }

        // Keys
        sb.append(BOLD)
                .append("Keys")
                .append(BOLD)
                .append(BR_2);
        for (String taskKey : task.keys()) {
            sb.append("- ")
                    .append(taskKey)
                    .append(BR);
        }
        sb.append(BR);

        // Options
        sb.append(BOLD)
                .append("Options")
                .append(BOLD)
                .append(BR_2)
                .append(OPTIONS_TABLE_HEADER);
        for (Field taskOptField : taskOptFields) {
            TaskOpt taskOpt = taskOptField.getAnnotation(TaskOpt.class);
            sb.append("| ")
                    .append(taskOpt.value())
                    .append(TABLE_SEPARATOR)
                    .append(taskOptField.getType().getSimpleName())
                    .append(TABLE_SEPARATOR)
                    .append(taskOpt.required() ? "✓" : "✘")
                    .append(" |")
                    .append(BR);
        }
        sb.append(BR);

        // Example
        StringBuilder exampleSb = new StringBuilder("```yaml")
                .append(BR)
                .append("tasks:")
                .append(BR)
                .append("  - task: ")
                .append(task.keys()[0])
                .append(BR)
                .append("    opt:")
                .append(BR);
        for (Field taskOptField : taskOptFields) {
            TaskOpt taskOpt = taskOptField.getAnnotation(TaskOpt.class);
            String taskOptValue = taskOpt.example().equals(Task.NULL) ? "<VALUE>" : taskOpt.example();
            exampleSb.append("      ")
                    .append(taskOpt.value())
                    .append(": ")
                    .append(taskOptValue)
                    .append(BR);
        }
        exampleSb.append("```");
        sb.append(BOLD)
                .append("Example")
                .append(BOLD)
                .append(BR_2)
                .append(exampleSb)
                .append(BR_2);
    }

}
