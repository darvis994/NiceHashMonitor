package telegrambot;

/**
 * Created by Darvis on 15.11.2016.
 */
public class BotConfig {

    static String TELEGRAM_BOT_TOKEN = "private bot token";
    static String TELEGRAM_BOT_USERNAME = "username_bot";
    static String PRIVATE_CHAT_ID = "";

    public static String getBotToken(){
        return TELEGRAM_BOT_TOKEN;
    }
    public static String getUsername(){
        return TELEGRAM_BOT_USERNAME;
    }
    public static String getPrivateChatId(){
        return PRIVATE_CHAT_ID;
    }

}
