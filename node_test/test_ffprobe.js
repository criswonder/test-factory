console.log('let\'s begin');
var ffprobe = require('ffprobe'),
    ffprobeStatic = require('ffprobe-static');
 
ffprobe('./file.mp4', { path: ffprobeStatic.path }, function (err, info) {
  if (err) return done(err);
  console.log(info);
});
