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
import org.apache.http.client.HttpClient;
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
    private static final String OS = System.getProperty("os.name");
    public static String ANSI_RESET = "\u001B[0m";
    public static String ANSI_GREEN = "\033[32;1;2m";
    public static String ANSI_CYAN = "\033[36;1;2m";
    public static String ANSI_YELLOW = "\033[33;1;2m";
    private static StringBuilder sb = new StringBuilder();

    public static void main(String[] args)   {
        checkColorAvailable();
        System.out.print("Starting..");
        while (true) {
            try {
                printFarmStatus(args);
                Thread.sleep(10000);
            } catch (NullPointerException | InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * @return Full JSon string from URL.
     * */
    static String getRequest(String URL) {
        String output = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
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
     * Method print full console output for one screen update.
     * @param wallets - BTC wallet from nicehash.com
     * */
   private static void printFarmStatus(String[] wallets) {
       CURRENT_PRICE_BTC = PoloniexMonitor.getLastPricePoloniex(PoloniexMonitor.USD_BTC_PAIR);
       sb.append("USD/BTC: " + ANSI_CYAN + CURRENT_PRICE_BTC + ANSI_RESET + "\n");
       sb.append("ZEC/BTC: " + ANSI_CYAN + PoloniexMonitor.getLastPricePoloniex(PoloniexMonitor.BTC_ZEC_PAIR)
               + ANSI_RESET + "\n");
       for (String wallet : wallets) {
           sb.append("\n##################################################\n");
           sb.append("\n");
           String stringJsonStatus = getRequest(STATUS_URL + wallet);
           String stringJsonBalance = getRequest(BALANCE_URL + wallet);
           String stringJsonWorkers = getRequest(WORKERS_STATUS_URL + wallet + ALGORITHM_ID);
           Double unpaidBalanceBTC = Double.parseDouble(getCurrentBalance(stringJsonBalance));
           sb.append("Current speed: " + ANSI_GREEN + getCurrentSpeed(stringJsonStatus) + ANSI_RESET + " Sol/s \n");
           sb.append("Unpaid balance: " + unpaidBalanceBTC + " BTC (" + new BigDecimal(unpaidBalanceBTC
                   * CURRENT_PRICE_BTC).setScale(2, RoundingMode.UP).doubleValue() + " USD) \n");
           HashMap <String,String> map = getWorkersStats(stringJsonWorkers);
           sb.append("Quantity workers: " + map.size() + "\n");
           for (Map.Entry <String,String> mapEntry : map.entrySet()) {
               sb.append(mapEntry.getKey() + "  - " + mapEntry.getValue() + " Sol/s \n");
           }
       }
       clearConsole();
       System.out.println(sb.toString());
       sb.setLength(0);
   }

    static void checkColorAvailable(){
        if(!OS.equals("Windows 10")) {
            ANSI_RESET = "";
            ANSI_GREEN = "";
            ANSI_CYAN = "";
            ANSI_YELLOW = "";
        }
    }
    /**
     * Method clear console output. Used for update screen.
     * */
    static void clearConsole() {
        try {
            if (OS.contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
}


// Build jar: mvn clean compile assembly:single
