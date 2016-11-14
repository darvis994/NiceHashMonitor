import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by Darvis on 13.11.2016.
 */

public class NiceHashMonitor  {
    private static String STATUS_URL = "https://www.nicehash.com/api?method=stats.provider.ex&addr=";
    private static String BALANCE_URL = "https://www.nicehash.com/api?method=stats.provider&addr=";
    private static String WORKERS_STATUS_URL = "https://www.nicehash.com/api?method=stats.provider.workers&addr=";
    private static String ALGORITHM_ID = "&algo=24";
    private static Double CURRENT_PRICE_BTC;

    public static void main(String[] args)  {
        CURRENT_PRICE_BTC = PoloniexMonitor.getLastPricePoloniex(PoloniexMonitor.USD_BTC_PAIR);
        System.out.println("USD/BTC: " + CURRENT_PRICE_BTC);
        System.out.println("ZEC/BTC: " + PoloniexMonitor.getLastPricePoloniex(PoloniexMonitor.BTC_ZEC_PAIR));
        System.out.println();
        for (String wallet : args) {
            System.out.println("##################################################");
            printFarmStatus(wallet);
        }
    }

    /**
     * @return Full JSon string from URL.
     * */
    static String getRequest(String URL) {
        String output = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet getRequest = new HttpGet(URL);
            getRequest.addHeader("accept", "application/json");
            HttpResponse response = httpClient.execute(getRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }
            BufferedReader br = new BufferedReader (new InputStreamReader((response.getEntity().getContent())));
            while ((output = br.readLine()) != null) {
                return output;
            }
            httpClient.getConnectionManager().shutdown();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    /**
     * @param stringJson - getRequest(STATUS_URL + BTC_WALLET)
     * @return Total speed for BTC wallet.
     * */
    private static Double getCurrentSpeed(String stringJson) {
        try {
            JsonElement jElement = new JsonParser().parse(stringJson);
            JsonObject jObject = jElement.getAsJsonObject();
            jObject = jObject.getAsJsonObject("result").getAsJsonArray("current").get(0).getAsJsonObject();
            JsonArray jData = jObject.getAsJsonArray("data");
            JsonObject speedjObject = jData.get(0).getAsJsonObject();
            return Double.parseDouble(speedjObject.get("a").getAsString());
        } catch (NullPointerException e) {
            return 0.0;
        }
    }

    /**
     * @param stringJson - getRequest(BALANCE_URL + BTC_WALLET)
     * @return Total unpaid balance.
     * */
    private static String getCurrentBalance(String stringJson) {
       JsonElement jelement = new JsonParser().parse(stringJson);
       JsonObject jobject = jelement.getAsJsonObject();
       jobject = jobject.getAsJsonObject("result").getAsJsonArray("stats").get(0).getAsJsonObject();
       String balance = jobject.get("balance").getAsString();
       return balance;
    }


   /**
    * @param stringJson - getRequest(WORKERS_STATUS_URL + BTC_WALLET + ALGORITHM_ID)
    * @return Map of all workers and its current speed.
    * */
   private static HashMap getWorkersStats(String stringJson) {
       HashMap <String,String> mapWorkers = new HashMap <String, String>();
       JsonElement jelement = new JsonParser().parse(stringJson);
       JsonObject jobject = jelement.getAsJsonObject();
       JsonArray jsonArray = jobject.getAsJsonObject("result").getAsJsonArray("workers");
       int i = 1;
       for (JsonElement element : jsonArray) {
           String workerName = element.getAsJsonArray().get(0).getAsString();
           String workerSpeed = element.getAsJsonArray().get(1).getAsJsonObject().get("a").getAsString();
           mapWorkers.put(i + ". " + workerName, workerSpeed);
           i++;
       }
       return mapWorkers;
   }

    /**
     * @param wallet - BTC wallet from nicehash.com
     * */
   private static void printFarmStatus(String wallet) {
       String stringJsonStatus = getRequest(STATUS_URL + wallet);
       String stringJsonBalance = getRequest(BALANCE_URL + wallet);
       String stringJsonWorkers = getRequest(WORKERS_STATUS_URL + wallet + ALGORITHM_ID);
       Double unpaidBalanceBTC = Double.parseDouble(getCurrentBalance(stringJsonBalance));
       System.out.println("Current speed: " + getCurrentSpeed(stringJsonStatus) + " Sol/s");
       System.out.println("Unpaid balance: " + unpaidBalanceBTC + " BTC (" + new BigDecimal(unpaidBalanceBTC
               * CURRENT_PRICE_BTC).setScale(2, RoundingMode.UP).doubleValue() + " USD)");
       HashMap <String,String> map = getWorkersStats(stringJsonWorkers);
       System.out.println("Quantity workers: " + map.size());
       for (Map.Entry <String,String> mapEntry : map.entrySet()) {
           System.out.println(mapEntry.getKey() + "  - " + mapEntry.getValue() + " Sol/s");
       }
   }
}



// Build jar: mvn clean compile assembly:single
