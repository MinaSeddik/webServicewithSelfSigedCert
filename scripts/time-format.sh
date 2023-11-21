#!/bin/bash

echo "'time' command is used to get the program execution time"
echo -ne "Example:\ntime cat /etc/passwd\n\n"

echo "Time Format: default"
time

echo "Time Format: after setting TIMEFORMAT=%R"

TIMEFORMAT=%R
time
