#!/bin/bash

myfunction() {

echo "======================================="
  echo "Hello from inside myfunction()"

  echo "Number of arguments passed to me = "$#
  echo "The arguments passed to me = "$*

  echo "The first argument passed to me = "${1}

  echo "Exit myfunction()"
  echo "======================================="
}

# calling the function with NO arguments
myfunction

# calling function with 1 arg
myfunction 56

# calling function with 2 arg
myfunction "simple arg" 56


