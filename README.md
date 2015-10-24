# SonicGesture
This is the project for ECE454 at UW-Madison Fall2015.

Contributors including:
Xuyi Ruan, Yunhe Liu, Wenxuan Mao and Kefei Fu

Initial Commit on 10/5/2015

## Announcement: 

- Meeting **Every Saturday @1:00pm** through Skype, should be very short, please plan accordingly: 
- Be ready to talk about what individual/group has accomplished (get prepares for that also helps individual as well in the weekly report)
- Be ready to discuss allocation of tasks for coming week. 
- Be ready to discuss code written and merge code in Github to master branch if necessary. 


## Weekly Updates: 

#### 10/12 Individual Milestones: 

**Kefei**
- create simple testing activity with basic UI  
- create and modify bytes[] containing sine wave   
- use audiotrack to play sine wave pure sound  
- try using audiorecord to record sound wave  

**Wenxuan**
- Looked up Android API to familiarize with music and sound playback control.
- Implemented Audio playback and looping, tested on mobile device.
- Tested microphone feature, tried to captured microphone stream.
	
**Xuyi** 
- Coded up recorder with functionality of both recording and replay.
- git repo: https://github.com/ruanxuyi/Recorder.git
- Assigned groups and discussed individual tasks in more detail. 
- Will integrate the recorder to the master branch later this week.
 
**Yunhe**
- Implemented audio player play/resume functionality upon Wenxuan’s player and tested on mobile device
- git: <https://github.com/ruanxuyi/SonicGesture>
- Assign group tasks in more detail and refined timeline and refined group logistics.



## Reference && Userful Links: 

### Papers: 

[SoundWave: Using the Doppler Effect to Sense Gestures](http://research.microsoft.com/en-us/um/redmond/groups/cue/publications/guptasoundwavechi2012.pdf)

### Recorder & Player:

[AudioRecord object not initializing](http://stackoverflow.com/questions/4843739/audiorecord-object-not-initializing)

[Android AudioRecord example](http://stackoverflow.com/questions/8499042/android-audiorecord-example)

[MediaRecorder](http://developer.android.com/reference/android/media/MediaRecorder.html)

[Example: Record audio and play the recorded audio](http://developer.android.com/guide/topics/media/audio-capture.html) 

### FFt Signal Processing: 


[Record and process in FFT](http://stackoverflow.com/questions/16982623/android-app-to-record-sound-in-real-time-and-identify-frequency)

[JTransforms](https://sites.google.com/site/piotrwendykier/software/jtransforms)

[Android 实时录音和回放,边录音边播放 (KTV回音效果)](http://www.cnblogs.com/mythou/p/3241925.html)

### Git

[Push commits to another branch](http://stackoverflow.com/questions/13897717/push-commits-to-another-branch)

### Git
[resolve merge conflict by hard resetting local workplace with master status](http://stackoverflow.com/questions/15127078/git-pull-is-not-possible-unmerged-files)
```
- git fetch origin
- git reset --hard origin/master
```
