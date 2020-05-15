import groovy.io.FileType


def list = []

//创建文件列表的list
def dir = new File("./")
dir.eachFileRecurse (FileType.FILES) { file ->
  list << file
}


def isThisFileInTheDir(ArrayList fileNames,ArrayList dirFiles){
    fileNames.each{ line ->
        boolean findResult = false
        for(int i=0;i<dirFiles.size();i++){
            if(dirFiles.get(i).toString().endsWith(line)){
                println "binggo!!!"
                findResult = true
                break;
            }else{
                continue;
            }
        }
        if(!findResult){
            println "fk!!!!====>"+line
        }

    }

    return null
}



f = new File("vendor_owner_info.txt")
if (f.length() > 0){
   pids = new ArrayList()

   f.eachLine { line -> 
//       println line

       //找到匹配的文件名称
       //例如 "system/vendor/firmware/abc.txt:moto" 将拿到abc.txt
       start = line.lastIndexOf("/")
       end = line.indexOf(":")
       lineMatch = line.substring(start+1,end)
//       println lineMatch
       pids.add(lineMatch)

       
   }
   
   isThisFileInTheDir(pids,list)
}
else{
   println "File is empty!"
}

println "============================================================="
println "============================this is seprator================="
println "============================================================="
f = new File("proprietary-blobs.txt")
if (f.length() > 0){
    pids = new ArrayList()

    f.eachLine { line ->
//       println line

        //找到匹配的文件名称
        //例如 "system/vendor/firmware/abc.txt:moto" 将拿到abc.txt
        start = line.lastIndexOf("/")
        lineMatch = line.substring(start+1)
//       println lineMatch
        pids.add(lineMatch)


    }

    isThisFileInTheDir(pids,list)
}
else{
    println "File is empty!"
}