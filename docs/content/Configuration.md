## Configuration

### SSH Configuration

> SSH is the core of Deployrr as every task is executed using the protocol.
> You will need to specify connection details as well as one method of authentication.

The SSH connection details are specified like so:

```yaml
ssh:
  host: <HOST_IP_OR_DOMAIN>
  user: <USER_NAME>
  password: <USER_PASSWORD>
```

#### Certificate authentication

> To make use of certificate based authentication with Deployrr, you first need an async keypair.
> If you don't have one prepared, use a tool like ssh-keygen.
> Your public key must be registered with the host you want to deploy to.

In order to enable certificate based authentication, simply switch out the `password` property
with `privateKey` and provide the path to your private key file (e.g. *~/.ssh/id_rsa* or *C:/Users/TEST/.ssh/id_rsa*).

```yaml
ssh:
  privateKey: <PATH_TO_PRIVATE_KEY>
```
