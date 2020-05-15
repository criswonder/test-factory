#!/bin/bash
declare -i i=1
while ((i<10))
do
  #echo `adb shell dumpsys meminfo com.taobao.ifalbumexample |grep Graphics`
  # echo `adb shell dumpsys meminfo com.taobao.idlefish.debug |grep Graphics`
  echo `adb shell dumpsys meminfo favdemo.taobao.com.fav_demo |grep Graphics`
  #echo `adb shell dumpsys meminfo taobao.com.idlefish_mm_demo |grep Graphics`
done 
