var fs=require('fs');

fs.readFile('3.json',function(err,data){
	if(err)
		throw err;

	var jsonObj=JSON.parse(data);
	var space=' ';
	var newLine=' . ';
	var chunks=[];
	var length=0;
	// console.log(jsonObj);
	
	var jsonObj2 = jsonObj['frames'];

	var keyFrameCount = 0;
	var totalFrame = 0;
	var keyFrameIndexs = [];
	for(var i=0,size=jsonObj2.length;i<size;i++){
		// console.log(jsonObj2[i]);
		var isVideo = jsonObj2[i]['media_type']=='video';
		// console.log(isVideo);

		if(isVideo){
			
			var isKeyFrame = jsonObj2[i]['key_frame']!=0;
			if(isKeyFrame){
				keyFrameCount++;
				keyFrameIndexs.push(i);
			}

			totalFrame ++;
		}
	}
	console.log(keyFrameIndexs)
	console.log("keyFrameCount="+keyFrameCount+",totalFrame="+totalFrame+",keyFrameIndexs="+keyFrameIndexs);
	
	 
});
