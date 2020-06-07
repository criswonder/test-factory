import time

#python3 adb_touch.py

while True:
    import subprocess

    order = 'adb -s afb3cdc5 shell input swipe 100 1800 100 800'  # 获取连接设备

    pi = subprocess.Popen(order, shell=True, stdout=subprocess.PIPE)
    time.sleep(0.5)
    pi = subprocess.Popen(order, shell=True, stdout=subprocess.PIPE)
    time.sleep(0.5)
    pi = subprocess.Popen(order, shell=True, stdout=subprocess.PIPE)
    time.sleep(0.5)
    # order = 'adb -s d2d36ef5 shell input tap 200 1800'  # 获取连接设备
    # pi = subprocess.Popen(order, shell=True, stdout=subprocess.PIPE)
    # print(pi.stdout.read())  # 打印结果
    # time.sleep(1)
# from uiautomator import Device
#
# d = Device('d2d36ef5')
# d.screenshot("1223.png")
