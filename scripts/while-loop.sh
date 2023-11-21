#!/bin/bash

TIMEFORMAT=%R

while1_function() {
  i=0
  total_elapsed=0
  while [ $i -lt 10 ]; do
#    echo "inside while1 function, iteration: "$i
    time_elapsed=$( (time cat /etc/passwd &>/dev/null) |& awk '{ print $1 * 1000 }')

    total_elapsed=$(($total_elapsed + $time_elapsed))

    # increment $i
    i=$(($i + 1))
  done

  echo "Total elapsed time: $total_elapsed seconds"
}

# call the function
while1_function
