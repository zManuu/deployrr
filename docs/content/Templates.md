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
