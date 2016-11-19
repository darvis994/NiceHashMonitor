package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/**
 * Created by HOME on 19.11.2016.
 */
public class MonitorConfig {
    public static boolean TELEGRAM_BOT_ENABLE;
    public static String TELEGRAM_BOT_TOKEN;
    public static String TELEGRAM_BOT_USERNAME;
    public static String PRIVATE_CHAT_ID;
    public static int DELAY_UPDATE_SECONDS;
    public static String[]BTC_WALLETS;
    public static double ALARM_HASHRATE;


    static {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream("config.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TELEGRAM_BOT_ENABLE = Boolean.parseBoolean(properties.getProperty("TELEGRAM_BOT_ENABLE").trim());
        TELEGRAM_BOT_TOKEN = properties.getProperty("TELEGRAM_BOT_TOKEN").trim();
        TELEGRAM_BOT_USERNAME = properties.getProperty("TELEGRAM_BOT_USERNAME").trim();
        PRIVATE_CHAT_ID = properties.getProperty("PRIVATE_CHAT_ID").trim();
        DELAY_UPDATE_SECONDS = Integer.parseInt(properties.getProperty("DELAY_UPDATE_SECONDS").trim());
        BTC_WALLETS = properties.getProperty("BTC_WALLETS").split(",");
        ALARM_HASHRATE =  Double.parseDouble(properties.getProperty("ALARM_HASHRATE"));

        for (int i = 0; i < BTC_WALLETS.length; i++) {
            BTC_WALLETS[i] = BTC_WALLETS[i].trim();
        }
    }


}
