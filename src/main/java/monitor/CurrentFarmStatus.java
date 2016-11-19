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
    private StringBuilder currenStatusMessage = new StringBuilder();


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
        return currenStatusMessage;
    }

    public void updateStatusTextMessage() {
        currenStatusMessage.setLength(0);
        for (Map.Entry<String,Double> entry : mapTotalWalletHashrate.entrySet()) {
            currenStatusMessage.append(entry.getKey().substring(0,5)
                    + "******" + entry.getKey().substring(28) + " : " + entry.getValue() + " Sol/s \n");
        }
    }


    public void updateHashrate(String btcWallet, Double hashrate) {
        mapTotalWalletHashrate.put(btcWallet, hashrate);
    }


    @Override
    public void run() {

        if(MonitorConfig.TELEGRAM_BOT_ENABLE)
            new Thread(new TelegramBot(this)).start();

        while (true) {t
            try {
                Thread.currentThread().sleep(MonitorConfig.DELAY_UPDATE_SECONDS * 1000 + 1500);
                updateStatusTextMessage();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
