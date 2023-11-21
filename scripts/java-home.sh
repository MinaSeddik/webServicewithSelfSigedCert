#!/bin/bash

JAVA_HOME=$(dirname $(dirname $(readlink -f $(which javac))))

echo $JAVA_HOME
echo $JAVA_HOME/bin/java