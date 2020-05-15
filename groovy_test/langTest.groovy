
//use[] to access a object property
class Test{
    public String str1;

}

def obj1 = new Test();
obj1.str1 = "you see me!!";
println obj1["str1"]

//define a map
def map1 = [str1:"abc"]
println "${map1.str1}"


//两个array相加
def array1 = ["xxx","bbb"]
def array2 = ["x","cc"]
print array1 + array2