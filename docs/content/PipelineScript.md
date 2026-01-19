```sh
#!/bin/bash

export DEPLOYRR_DOWNLOAD="https://github.com/zManuu/deployrr/releases/download/v0.0.2/Deployrr-0.0.2-SNAPSHOT.jar"
export DEPLOYRR_DIR="$HOME/deployrr"
export DEPLOYRR_JAR="$DEPLOYRR_DIR/deployrr.jar"
export DEPLOYRR_FILE=$1
export JRE_DIR="$DEPLOYRR_DIR/jre"
export JRE_EXE="$JRE_DIR/openlogic-openjdk-jre-11.0.29+7-linux-x64/bin/java"
export JRE_DOWNLOAD="https://builds.openlogic.com/downloadJDK/openlogic-openjdk-jre/11.0.29+7/openlogic-openjdk-jre-11.0.29+7-linux-x64.tar.gz"
export JRE_ARCHIVE="$DEPLOYRR_DIR/jre.tar.gz"

# Installation
if [ ! -e $DEPLOYRR_DIR ]; then
  echo "Creating Deployrr home..."
  mkdir $DEPLOYRR_DIR
fi
if [ ! -e $JRE_DIR ]; then
  echo "Installing Java 11..."
  mkdir $JRE_DIR
  wget $JRE_DOWNLOAD -O $JRE_ARCHIVE
  tar -xzf $JRE_ARCHIVE -C $JRE_DIR
  rm $JRE_ARCHIVE
fi
if [ ! -f $DEPLOYRR_JAR ]; then
  echo "Installing Deployrr..."
  wget $DEPLOYRR_DOWNLOAD -O $DEPLOYRR_JAR
fi

# Launch
echo "Launching Deployrr..."
$JRE_EXE -jar $DEPLOYRR_JAR -f $DEPLOYRR_FILE
```