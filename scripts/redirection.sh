#!/bin/bash


echo "< : Gives input to a command."
echo "command < file.txt"

# example
# awk '{ print $1}' < /home/mina/myfile.txt

echo "==================================="
echo "> : Directs the output of a command into a file."
echo "command > out.txt"

echo "==================================="
echo ">> : Does the same as >, except that if the target file exists, the new data are appended."
echo "command >> out.txt"

echo "command >out.txt 2>error.txt"
echo "command < file.txt > out.txt 2> error.txt"

echo "==================================="
echo ">> : Does the same as >, except that if the target file exists, the new data are appended."
echo "command >> out.txt"

echo "==================================="
echo ">&, &>, >>& and &>> : (read above also) Redirect both standard error and standard output, replacing or appending, respectively."
