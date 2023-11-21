#!/bin/bash

# to set env variable
export TIMEFORMAT=%R

# unset env variable
unset TIMEFORMAT

# to list env variables
env

# set JAVA_HOME explicitly
JAVA_HOME=$HOME/VMs/jdk-11.0.5

# set GRAAL_HOME explicitly
GRAAL_HOME=$HOME/VMs/graalvm-ee-19.2.1
