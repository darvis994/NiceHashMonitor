package telegrambot;

import config.MonitorConfig;
import monitor.CurrentFarmStatus;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by Darvis on 15.11.2016.
 */
public class TelegramBot extends TelegramLongPollingBot implements Runnable {
    private CurrentFarmStatus currentFarmStatus;

    public TelegramBot(CurrentFarmStatus currentFarmStatus) {
        this.currentFarmStatus = currentFarmStatus;
    }

    @Override
    public void onUpdateReceived(Update update) {
        //check if the update has a message
        if(update.hasMessage()) {
            Message message = update.getMessage();
            //check if the message has text. it could also contain for example a location ( message.hasLocation() )
            if(message.hasText()) {
                String responseMessage = null;
                String inputMessage = message.getText();
                if(inputMessage.contains("getspeed")) {
                    responseMessage = currentFarmStatus.getStatusTextMessage().toString();
                } else if (inputMessage.contains("getalarmspeed")) {
                    responseMessage = "Alarm hashrate = " + String.valueOf(MonitorConfig.ALARM_HASHRATE) + " Sol/s";
                } else if(inputMessage.contains("go"))
                    responseMessage = "Gogogogogo go go go go go fast go!";
                else return;
                // response to same chat room where the message came.
                sendMessageTo(String.valueOf(message.getChatId()), responseMessage);
            }
        }
    }

    public void sendMessageTo(String chatID, String messageText) {
        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.setChatId(chatID); //who should get from the message the sender that sent it.
        sendMessageRequest.setText(messageText);
        try {
            sendMessage(sendMessageRequest);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return MonitorConfig.TELEGRAM_BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return MonitorConfig.TELEGRAM_BOT_TOKEN;
    }


    @Override
    public void run() {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
