#!/bin/bash
declare -i i=1
while ((i<10))
do
   echo `adb shell dumpsys meminfo favdemo.taobao.com.fav_demo|grep TOTAL`
done 
