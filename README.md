# Deployrr

> The all-in-one tool for quickly setting up configurable deployments and whole environments.

## Documentation

The **full documentation** can be viewed under [134.199.188.73](http://134.199.188.73)

## Quick start

For automatic deployment in a **pipeline**, a script has been prepared (for Linux environments).  
It automates the installation of the JRE and Deployrr and launches the deployment.

[Preview Deployrr.sh](content/PipelineScript.md)  
<a href="Deployrr.sh" download>Deployrr.sh</a>

In a pipeline, simply execute the Deployrr.sh.  
As the argument, pass the file path of your `deployrr.yaml` or `deployrr.json`.  
Head to [templates](#templates) for an overview of `deployrr.yaml` examples.

```bash
bash <(curl -s http://134.199.188.73/Deployrr.sh) <PathToDeployrrDotJsonOrYaml>
```

## Templates

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

