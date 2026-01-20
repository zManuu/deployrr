## Templates

To help you get started with Deployrr, there is a list of templates prepared for you.
Just copy the one closest to your needs and edit it.

### Docker Compose

Copies a `docker-compose.yml` to the remote and starts the Docker Compose.

```yaml
ssh:
  host: ???
  user: ???
  password: ???
tasks:
  - task: cp
    name: Copy Docker Compose
    opt:
      source: deployrr-docker-compose.yml
      target: /deployment/
  - task: docker-compose-up
    name: Start Docker Compose
    opt:
      location: /deployment/deployrr-docker-compose.yml
```

### Script execution

Copies a script to the remote and executes it.

```yaml
ssh:
  host: ???
  user: ???
  password: ???
tasks:
  - task: cp
    name: Copy Script
    opt:
      source: script.sh
      target: /deployment/
      chmod: +x
  - task: cmd
    name: Execute Script
    opt:
      cmd: /deployment/script.sh
```

## Task dependencies

In order to ensure a certain task execution order, there are two things to consider:

1. The execution order normally follows the order specified in the deployrr-file.
2. By using the `depends` parameter in a task definition, you may specify tasks that need to be executed beforehand.

```yaml
tasks:
  - task: cmd
    name: Execute Script
    depends:
      - Copy Script
    opt:
      cmd: /deployment/script.sh
  - task: cp
    name: Copy Script
    opt:
      source: script.sh
      target: /deployment/
      chmod: +x
```

This will result in the execution order `[Copy Script, Execute Script]`.  
⚠️ **Note**: Every task is unique. It is currently not possible to define one Task and make it execute multiple times.
By depending on one task in multiple tasks, the dependent task will always be executed only once according to the execution order.
