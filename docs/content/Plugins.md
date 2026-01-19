## Plugins

Deployrr plugins enable you to embed your own task definitions and logic into Deployrr.  
A few quick steps are necessary in order to set up a working plugin:

1. Copy the [Deployrr plugin example](https://github.com/zManuu/deployrr/tree/main/deployrr-plugin-example).
2. Open the example with your IDE of choice.
3. Please have a look into [ExamplePlugin.java](https://github.com/zManuu/deployrr/blob/main/deployrr-plugin-example/src/main/java/com/deployrr/pluginexample/ExamplePlugin.java) and [CustomTask.java](https://github.com/zManuu/deployrr/blob/main/deployrr-plugin-example/src/main/java/com/deployrr/pluginexample/CustomTask.java).
4. Create your own task :)
5. Build the plugin using `mvn package`.
6. Install the plugin by placing the jar file into the plugins directory (`~/deployrr/plugins`).

### Task Definition

You can create custom tasks by following this blueprint:

```java
@Task(name = "Custom Task", keys = {"custom-task"})
public class CustomTask extends DeployTask {

    public CustomTask(SSHConnection sshConnection, String name) {
        super(sshConnection, name);
    }

    @Override
    public TaskResult execute() throws TaskException {
        return TaskResult.success();
    }

}
```

> ⚠️ In your implementation of `DeployrrPlugin`, you must specify all custom task classes in order for them to be embedded into Deployrr.

### Task options

Your task can have multiple options. To use an option for your custom task, copy this snippet into your task class:

```java
@TaskOpt("OPT")
private String opt;
```

There are several supported java types for options: `String`, `Integer`, `Long`, `Float`, `Double`, `Boolean`

### Task usage

With your plugin installed, you can make use of your custom task like with all others:

```yaml
tasks:
  - task: custom-task
    name: Execute custom task
    opt:
      opt: Hello world
```
