cd /Users/hongyun/source/personal/
for dir in `ls`; do                  #历遍ls命令显示目录
    echo ========================================
    echo $dir
    if [ -d "$dir" ]; then
        cd $dir;
        git status;
        cd ../
    fi
done