## Tasks

### General options

There are a few special options build into Deployrr. You can use those options on every task definition.

| Option | Type | Description | Required | Default |
|---|---|---|---|---|
| ignore_failure | Boolean | Ignore the potential failure of a task and continue with the next task. | ✘ | false |

### Remove

Removes a file or directory on the remote.

**Keys**

- rm
- remove
- delete

**Options**

| Option | Type | Description | Required | Default |
|---|---|---|---|---|
| location | String |  | ✓ |  |
| force | Boolean |  | ✘ | false |
| recursive | Boolean |  | ✘ | false |

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

| Option | Type | Description | Required | Default |
|---|---|---|---|---|
| target | String |  | ✓ |  |
| chmod | String |  | ✘ |  |
| source | String |  | ✓ |  |

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

| Option | Type | Description | Required | Default |
|---|---|---|---|---|
| cwd | String |  | ✘ |  |
| cmd | String |  | ✓ |  |

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

| Option | Type | Description | Required | Default |
|---|---|---|---|---|
| pull_policy | String |  | ✘ | missing |
| remove_orphans | Boolean |  | ✘ | true |
| demon | Boolean |  | ✘ | true |
| location | String |  | ✓ |  |

**Example**

```yaml
tasks:
  - task: dockercomposeup
    opt:
      pull_policy: always
      remove_orphans: <VALUE>
      demon: <VALUE>
      location: /deployment/docker-compose.yaml
```

### Timeout

Makes the deployment pause for a specified time period.

**Keys**

- timeout
- time-out
- sleep

**Options**

| Option | Type | Description | Required | Default |
|---|---|---|---|---|
| time | Integer |  | ✓ |  |
| unit | String | Time unit: MILLISECONDS / SECONDS / MINUTES / HOURS | ✘ | MILLISECONDS |

**Example**

```yaml
tasks:
  - task: timeout
    opt:
      time: 500
      unit: SECONDS
```

### Make directory

Creates a directory on the remote.

**Keys**

- mkdir

**Options**

| Option | Type | Description | Required | Default |
|---|---|---|---|---|
| dir | String |  | ✓ |  |

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

| Option | Type | Description | Required | Default |
|---|---|---|---|---|
| location | String |  | ✓ |  |

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

| Option | Type | Description | Required | Default |
|---|---|---|---|---|
| url | String |  | ✓ |  |
| location | String |  | ✓ |  |

**Example**

```yaml
tasks:
  - task: gitclone
    opt:
      url: https://github.com/zManuu/deployrr.git
      location: /deployment/deployrr-repo
```

### Health Check

Performs an HTTP-request with CURL and checks the status-code.

**Keys**

- health
- healthcheck
- health_check
- health-check

**Options**

| Option | Type | Description | Required | Default |
|---|---|---|---|---|
| url | String | Fully qualified url. | ✘ |  |
| method | String | Http method: GET / HEAD / OPTIONS / ... | ✘ | GET |
| port | Integer | Port of the service. Used in combination with 'path'. | ✘ |  |
| expected | Integer | The expected HTTP status-code. | ✘ | 200 |
| path | String | Path in the service. Used in combination with 'port'. | ✘ |  |

**Example**

```yaml
tasks:
  - task: health
    opt:
      url: http://localhost:8080/health
      method: GET
      port: 8080
      expected: 200
      path: health
```

