### Manual deployment

1. Make sure you have installed **Java 11** on your machine.
2. Create a working directory on your machine for the project (or use an existing one).
3. Head to the [Releases overview on GitHub](https://github.com/zManuu/deployrr/releases).
4. Click on *Assets* under the latest Release.
5. Download the *Deployrr-???.jar* and place it into your working directory.
6. Download a [template](#Templates) and place it into your working directory.
7. Edit the template to your desires.
8. Run the deployment by executing the jar file:

```bash
java -jar <Deployrr-???.jar>
```

### Pipeline deployment

For automatic deployment in a pipeline, a script has been prepared (for Linux environments).  
It automates the installation of the JRE and Deployrr and launches the deployment.

[Preview Deployrr.sh](content/PipelineScript.md)  
<a href="Deployrr.sh" download>Deployrr.sh</a>  

In a pipeline, simply execute the Deployrr.sh.  
As the argument, pass the file path of your `deployrr.yaml` or `deployrr.json`.

```bash
bash <(curl -s http://134.199.188.73/Deployrr.sh) <PathToDeployrrDotJsonOrYaml>
```
