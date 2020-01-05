@ECHO OFF
echo trying to run loopMidi.exe from this folder, 
echo modify the run.bat file if you want to start something else or from another path...
start ./loopMIDI.exe
echo starting server now
java -jar ./7PadMidi.jar %1 %2
PAUSE