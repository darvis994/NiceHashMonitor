package monitor;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Created by Darvis on 14.11.2016.
 */

public class PoloniexMonitor {

    public static String POLONIEX_ORDERBOOK_URL = "https://poloniex.com/public?command=returnOrderBook&currencyPair=";
    public static String USD_BTC_PAIR = "USDT_BTC";
    public static String USD_ZEC_PAIR = "USDT_ZEC";
    public static String BTC_ZEC_PAIR = "BTC_ZEC";



    /**
     * @param pair Name of currency pair from poloniex.
     *             Check https://poloniex.com/public?command=returnOrderBook&currencyPair=BTC_ZEC&depth=1
     * @return
     */
    public static double getLastPricePoloniex(String pair) {
        JsonElement jelement = new JsonParser().parse(NiceHashMonitor.getRequest(POLONIEX_ORDERBOOK_URL
                + pair + "&depth=1"));
        String price = jelement.getAsJsonObject().get("bids").getAsJsonArray()
                .get(0).getAsJsonArray().get(0).getAsString();
        return Double.parseDouble(price);
    }
}
