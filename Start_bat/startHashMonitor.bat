@echo off
chcp 866 >nul
set time=30
:loop
cls
@java -jar NiceHashMonitor.jar BTC_WALLET_1 BTC_WALLET_2 BTC_WALLET_N


ping 127.0.0.1 -n %time% >nul
Goto :loop
