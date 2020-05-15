#!/bin/bash
declare -i i=1
while ((i<10))
do
  #  echo `adb shell dumpsys meminfo com.taobao.idlefish.debug|grep Graphics`
  #  echo `adb shell dumpsys meminfo com.taobao.idlefish|grep Graphics`
   echo `adb shell dumpsys meminfo com.taobao.ifalbumexample|grep Graphics`
  #  echo `adb shell dumpsys meminfo favdemo.taobao.com.fav_demo|grep Graphics`
  # echo `adb shell dumpsys meminfo com.taobao.fmm.demo|grep Graphics`
  #echo `adb shell dumpsys meminfo taobao.com.idlemmdemo|grep Graphics`
  
  
  # gpuMemory=`adb shell dumpsys meminfo com.taobao.idlefish |grep Graphics`
  # gpuMemoryTrim=${gpuMemory/"Graphics:    "/""}
  # echo $gpuMemoryTrim
  # let "num=gpuMemoryTrim/1024"
  # echo $num"M"

  # javaHeap=`adb shell dumpsys meminfo com.taobao.idlefish |grep Java\ Heap`
  # javaHeap=${javaHeap/"Java Heap: "/""}
  # let "javaHeap=javaHeap/1024"
  # # echo "$javaHeap"


  # nativeHeap=`adb shell dumpsys meminfo com.taobao.idlefish |grep Native\ Heap:`
  # # echo $nativeHeap
  # nativeHeap=${nativeHeap/"Native Heap: "/""}
  # let "nativeHeap=nativeHeap/1024"
  # # echo "$nativeHeap"

  # echo "Native:"$nativeHeap"M,GPU:"$num"M,Java:"$javaHeap"M"

done 
