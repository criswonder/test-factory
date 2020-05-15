#!/bin/bash
for i in $( ls ); do
    echo item: $i
done

array=(1,222,3,4)
for i in $array 
do
    echo item: $i
done

echo ${#array[1]}
