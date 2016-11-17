package monitor;

import telegrambot.TelegramBot;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HOME on 17.11.2016.
 */
public class CurrentFarmStatus implements Runnable {
    private Map <String,Double> mapTotalWalletHashrate;


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
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String,Double> entry : mapTotalWalletHashrate.entrySet()) {
            stringBuilder.append(NiceHashMonitor.ANSI_CYAN + entry.getKey().substring(0,5)
                    + "******" + entry.getKey().substring(28) + " : " + entry.getValue()
                    + " Sol/s" + NiceHashMonitor.ANSI_RESET + "\n");
        }
        return stringBuilder;
    }

    public void updateHashrate(String btcWallet, Double hashrate) {
        mapTotalWalletHashrate.put(btcWallet, hashrate);
    }


    @Override
    public void run() {
        TelegramBot telegramBot = new TelegramBot(this);
        Thread threadTelegram = new Thread(telegramBot);
        threadTelegram.start();
        while (true) {
            try {
                Thread.currentThread().sleep(22000);
                System.out.println(getStatusTextMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
