## Arguments

There are several arguments you can pass to Deployrr.
They give you finer control over the engine and its configuration.
Note that all arguments are optional.

| **Name**      | **Flag** | **Description**               | **Example**     |
|---------------|----------|-------------------------------|-----------------|
| Deployrr File | -f       | Specify a deployrr file path. | /deployrr.yaml  |
| Verbose       | -v       | Enable verbose logging.       |                 |
| No Banner     | -b       | Disable the Deployrr banner.  |                 |

To use arguments, pass them when running the jar like so:

```bash
java -jar <Deployrr-???.jar> -f <PathToDeployrrDotJsonOrYaml> -v -b
```
