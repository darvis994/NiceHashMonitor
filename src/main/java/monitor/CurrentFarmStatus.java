package monitor;

import config.MonitorConfig;
import telegrambot.TelegramBot;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Darvis on 17.11.2016.
 */
public class CurrentFarmStatus implements Runnable {
    private Map <String,Double> mapTotalWalletHashrate;
    private StringBuilder currentStatusMessage = new StringBuilder();
    private TelegramBot telegramBot;


    public CurrentFarmStatus(String[]BTCwallets) {
        initFarm(BTCwallets);
    }

    private void initFarm(String[] BTCwallets) {
        mapTotalWalletHashrate = new HashMap<>();
        for (String wallet : BTCwallets) {
            mapTotalWalletHashrate.put(wallet, 0.0);
        }
    }

    public StringBuilder getStatusTextMessage() {
        return currentStatusMessage;
    }

    public void updateStatusTextMessage() {
        currentStatusMessage.setLength(0);
        for (Map.Entry<String,Double> entry : mapTotalWalletHashrate.entrySet()) {
            currentStatusMessage.append(entry.getKey().substring(0,5)
                    + "******" + entry.getKey().substring(28) + " : " + entry.getValue() + " Sol/s \n");
        }
    }


    public void updateHashrate(String btcWallet, Double hashrate) {
        mapTotalWalletHashrate.put(btcWallet, hashrate);
    }

    public void checkAlarmHashrate() {
        for (Map.Entry<String, Double> hashrates : mapTotalWalletHashrate.entrySet()) {
            if(hashrates.getValue() < MonitorConfig.ALARM_HASHRATE) {
                String message = "\uD83D\uDEA7 Alarm! Total hashrate of " + hashrates.getKey().substring(0,5) + "******" +
                        hashrates.getKey().substring(28) + " equal " + hashrates.getValue() + " Sol/s \uD83D\uDEA7";
                telegramBot.sendMessageTo(MonitorConfig.PRIVATE_CHAT_ID, message);
            }
        }
    }

    @Override
    public void run() {

        if(MonitorConfig.TELEGRAM_BOT_ENABLE)
            telegramBot = new TelegramBot(this);
            new Thread(telegramBot).start();

        while (true) {
            try {
                Thread.currentThread().sleep(MonitorConfig.DELAY_UPDATE_SECONDS * 1000 + 1500);
                updateStatusTextMessage();
                if(MonitorConfig.TELEGRAM_BOT_ENABLE)
                    checkAlarmHashrate();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
