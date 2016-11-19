@echo off
chcp 866 >nul
set time=30
:loop
cls
@java -jar monitor.NiceHashMonitor.jar


ping 127.0.0.1 -n %time% >nul
Goto :loop
