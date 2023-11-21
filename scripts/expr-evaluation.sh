#!/bin/bash

# Integer number manipulation

echo Integer number manipulation
result=$(( 5 * 6 ))
echo $result

X=15

result2=$((X * 6))
echo $result2

echo "12+5" | bc
echo "$X+5" | bc

echo Floating number manipulation

echo "0.01+5" | bc
echo "0.01*1000" | bc

echo "$X+5.58" | bc
echo "$X*5.58" | bc
echo "$X/3" | bc

val1=$(echo "$X+5.58" | bc)
val2=$(echo "$X*5.58" | bc)
val3=$(echo "$X/3" | bc)

echo $val1
echo $val2
echo $val3

