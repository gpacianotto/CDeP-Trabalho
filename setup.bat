@echo off

call javac Client/*.java
pause
call javac Server/*.java
pause
call javac ./compute/*.java
pause
call jar cvf ./classes/compute.jar compute/*.class
pause
call javac -cp ./classes/compute.jar engine/ComputeEngine.java
pause
call javac -cp ./classes/compute.jar Server/TCPServer.java Server/ImageManipulator.java Server/StringCryptographer.java Server/MusicNoteGenerator.java
pause
