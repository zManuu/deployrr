## Tasks

### General options

There are a few special options build into Deployrr. You can use those options on every task definition.

| Option | Type | Description | Required | Default |
|---|---|---|---|---|
| ignore_failure | Boolean | Ignore the potential failure of a task and continue with the next task. | ✘ | false |
| ignore_validation_hooks | Boolean | Ignore all validation hooks of a task (for instance, whether the file exists). | ✘ | false |

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

### Docker Compose Pull

Pulls images from a docker-compose.

**Keys**

- dockercomposepull
- docker_compose_pull
- docker-compose-pull

**Options**

| Option | Type | Description | Required | Default |
|---|---|---|---|---|
| include_deps | Boolean | Also pull services declared as dependencies | ✘ | false |
| ignore_buildable | Boolean | Ignore images that can be built | ✘ | false |
| policy | String | Apply pull policy: missing / always | ✘ | missing |
| ignore_pull_failures | Boolean | Pull what it can and ignores images with pull failures | ✘ | false |
| location | String | File-path of the docker-compose file. | ✓ |  |
| quiet | Boolean | Pull without printing progress information | ✘ | false |

**Example**

```yaml
tasks:
  - task: dockercomposepull
    opt:
      include_deps: <VALUE>
      ignore_buildable: <VALUE>
      policy: <VALUE>
      ignore_pull_failures: <VALUE>
      location: /deployment/docker-compose.yaml
      quiet: <VALUE>
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

### Git Clone

Clones a git repository on the remote.

**Keys**

- gitclone
- git-clone
- git_clone

**Options**

| Option | Type | Description | Required | Default |
|---|---|---|---|---|
| location | String |  | ✓ |  |
| url | String |  | ✓ |  |

**Example**

```yaml
tasks:
  - task: gitclone
    opt:
      location: /deployment/deployrr-repo
      url: https://github.com/zManuu/deployrr.git
```

### Docker Compose Up

Starts a docker-compose.

**Keys**

- dockercomposeup
- docker_compose_up
- docker-compose-up

**Options**

| Option | Type | Description | Required | Default |
|---|---|---|---|---|
| demon | Boolean | Start docker-compose as demon. | ✘ | true |
| location | String | File-path of the docker-compose file. | ✓ |  |
| remove_orphans | Boolean |  | ✘ | true |
| pull_policy | String |  | ✘ | missing |

**Example**

```yaml
tasks:
  - task: dockercomposeup
    opt:
      demon: <VALUE>
      location: /deployment/docker-compose.yaml
      remove_orphans: <VALUE>
      pull_policy: always
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
| source | String |  | ✓ |  |
| chmod | String |  | ✘ |  |
| target | String |  | ✓ |  |

**Example**

```yaml
tasks:
  - task: cp
    opt:
      source: deployment/start.sh
      chmod: +x
      target: /deployment/
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

### Remove

Removes a file or directory on the remote.

**Keys**

- rm
- remove
- delete

**Options**

| Option | Type | Description | Required | Default |
|---|---|---|---|---|
| recursive | Boolean |  | ✘ | false |
| location | String |  | ✓ |  |
| force | Boolean |  | ✘ | false |

**Example**

```yaml
tasks:
  - task: rm
    opt:
      recursive: true
      location: /deployment
      force: true
```

