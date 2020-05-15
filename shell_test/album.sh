#!/bin/bash
declare -i i=1
while ((i<10))
do
   echo `adb shell dumpsys meminfo com.taobao.ifalbumexample|grep Graphics`
done 
