@echo off

start rmiregistry
pause
start java -cp ./;./classes/compute.jar -Djava.rmi.server.codebase=file:./classes/compute.jar -Djava.rmi.server.hostname=localhost -Djava.security.policy=server-win.policy engine.ComputeEngine localhost 1099
pause
start java Server/TCPServer
pause
call java Client/TCPClient imageProcessor grayed Client/input.png 2
@REM call java Client/TCPClient crypt encrypt "message bla bla bla" senha123
@REM call java Client/TCPClient crypt decrypt "Z YYTO XKDTXKDTXKDT" senha123
@REM call java Client/TCPClient music-scale-generator generate A major
@REM call java Client/TCPClient music-scale-generator generate G minor
pause