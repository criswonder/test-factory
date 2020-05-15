#!/bin/sh

filename="/home"

if [ -d "$filename" ]; then

  echo "$filename is a directory "

elif [ -f "$filename" ]; then

  echo "$filename is a file"

fi