import java.util.ArrayList;
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

    public void updateHashrate(String btcWallet, Double hashrate) {
        mapTotalWalletHashrate.put(btcWallet, hashrate);
    }


    @Override
    public void run() {
        while (true) {
            try {
                Thread.currentThread().sleep(22000);
                for (Map.Entry<String,Double> entry : mapTotalWalletHashrate.entrySet()){
                    System.out.println(NiceHashMonitor.ANSI_CYAN + entry.getKey().substring(0,5)
                            + "*********************" + entry.getKey().substring(28) + " : " + entry.getValue()
                            + " Sol/s" + NiceHashMonitor.ANSI_RESET);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
