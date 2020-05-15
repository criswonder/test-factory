def command = "adb shell pm list packages"// Create the String
def proc = command.execute()                 // Call *execute* on the string
proc.waitFor()                               // Wait for the command to finish

// Obtain status and output
println "list packages->return code: ${ proc.exitValue()}"
println "list packages->stderr: ${proc.err.text}"
//println "stdout: ${proc.in.text}" // *out* from the external program is *in* for groovy

commandResult = proc.in.text.split("\n")

commandResult.each{
    if(!it.contains("android") && !it.contains("miui")){
//        println it
        pkg = it.split(":")[1]
        println pkg
        def c ="adb shell pm path ${pkg}"// Create the String

        println "shell command is: ${c}"
        def p = c.execute()                 // Call *execute* on the string
        p.waitFor() 
//        String getApkPath = p.in.text.split(":")[1]
        pkgPath = p.in.text.split(":")[1]
        println pkgPath
        
        
        // def c2 ="adb pull "+pkgPath+" ."+pkgPath// adb pull /data/app/com.tencent.mm-1/base.apk ./data/app/com.tencent.mm-1/base.apk

        def c2 ="adb pull "+pkgPath  // adb pull /data/app/com.tencent.mm-1/base.apk ./data/app/com.tencent.mm-1/base.apk
        def p3 = c2.execute()                 // Call *execute* on the string
        p3.waitFor() 
        
        println "adb pull ->return code: ${ p3.exitValue()}"
        println "adb pull ->stderr: ${p3.err.text}"
        println "adb pull ->stdout: ${p3.in.text}"
        
    }
}