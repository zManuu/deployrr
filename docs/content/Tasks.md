## Tasks

### Remove

Removes a file or directory on the remote.

**Keys**

- rm
- remove
- delete

**Options**

| Option | Type | Required |
|---|---|---|
| location | String | ✓ |
| force | Boolean | ✘ |
| recursive | Boolean | ✘ |

**Example**

```yaml
tasks:
  - task: rm
    opt:
      location: /deployment
      force: true
      recursive: true
```

### CopyFile

Copies a local file or directory to the remote.

**Keys**

- cp
- scp
- copy

**Options**

| Option | Type | Required |
|---|---|---|
| target | String | ✓ |
| chmod | String | ✘ |
| source | String | ✓ |

**Example**

```yaml
tasks:
  - task: cp
    opt:
      target: /deployment/
      chmod: +x
      source: deployment/start.sh
```

### Command

Executes a command on the remote.

**Keys**

- cmd
- exec
- command

**Options**

| Option | Type | Required |
|---|---|---|
| cwd | String | ✘ |
| cmd | String | ✓ |

**Example**

```yaml
tasks:
  - task: cmd
    opt:
      cwd: /deployment/
      cmd: script.sh
```

### DockerComposeUp

**Keys**

- dockercomposeup
- docker_compose_up
- docker-compose-up

**Options**

| Option | Type | Required |
|---|---|---|
| location | String | ✓ |

**Example**

```yaml
tasks:
  - task: dockercomposeup
    opt:
      location: /deployment/docker-compose.yaml
```

### Make directory

Creates a directory on the remote.

**Keys**

- mkdir

**Options**

| Option | Type | Required |
|---|---|---|
| dir | String | ✓ |

**Example**

```yaml
tasks:
  - task: mkdir
    opt:
      dir: /deployment
```

### Git Pull

Pulls a git repository on the remote.

**Keys**

- gitpull
- git-pull
- git_pull

**Options**

| Option | Type | Required |
|---|---|---|
| location | String | ✓ |

**Example**

```yaml
tasks:
  - task: gitpull
    opt:
      location: /deployment/deployrr-repo
```

### Git Clone

Clones a git repository on the remote.

**Keys**

- gitclone
- git-clone
- git_clone

**Options**

| Option | Type | Required |
|---|---|---|
| url | String | ✓ |
| location | String | ✓ |

**Example**

```yaml
tasks:
  - task: gitclone
    opt:
      url: https://github.com/zManuu/deployrr.git
      location: /deployment/deployrr-repo
```

### Health Check

**Keys**

- health
- healthcheck
- health_check
- health-check

**Options**

| Option | Type | Required |
|---|---|---|
| url | String | ✘ |
| method | String | ✘ |
| port | Integer | ✘ |
| expected | Integer | ✘ |
| path | String | ✘ |

**Example**

```yaml
tasks:
  - task: health
    opt:
      url: http://localhost:8080/health
      method: GET
      port: 8080
      expected: 202
      path: health
```

