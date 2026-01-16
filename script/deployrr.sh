#!/bin/bash

export DEPLOYRR_DIR="$HOME/deployrr"
export DEPLOYRR_JAR="$DEPLOYRR_DIR/deployrr.jar"
export DEPLOYRR_FILE=$1
export JRE_DIR="$DEPLOYRR_DIR/jre"
export JRE_EXE="$JRE_DIR/openlogic-openjdk-jre-11.0.29+7-linux-x64/bin/java"

./deployrr-install.sh
./deployrr-run.sh
