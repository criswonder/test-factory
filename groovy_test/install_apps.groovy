import groovy.io.FileType


def list = []

//创建文件列表的list
def dir = new File("./")
dir.eachFileRecurse (FileType.FILES) { file ->
  list << file
}


for(int i=0;i<list.size();i++){
            if(list.get(i).toString().endsWith("apk")){
//                println list.get(i)
                
                def c3 ="adb install ${list.get(i)}"// adb install ./data/app/com.Qunar-1/base.apk
                println "command is:"+ c3
                def p3 = c3.execute()                 // Call *execute* on the string
                p3.waitFor() 
                
                println "return code: ${ p3.exitValue()}"
                println "stderr: ${p3.err.text}"
                println "stdout: ${p3.in.text}"
                
            }else{
                 println  "skip:" + list.get(i)
            }
}