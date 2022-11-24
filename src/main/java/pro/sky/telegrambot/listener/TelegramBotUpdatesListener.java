package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exceptions.NotificationTimeAlreadyPastException;
import pro.sky.telegrambot.service.MessageHandler;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {


    private final MessageHandler messageHandler;

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);


    private final TelegramBot telegramBot;

    public TelegramBotUpdatesListener(MessageHandler messageHandler, TelegramBot telegramBot) {
        this.messageHandler = messageHandler;
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
                    logger.info("Processing update: {}", update);
                    if (update.message()==null){
                        return;
                    }
                    try {
                        createAndSaveTaskFromMessage(update.message());
                        sendResponceInChat(update.message().chat(), "Уведомление создано");

                    } catch (DateTimeParseException e) {
                        sendResponceInChat(update.message().chat(), "Введите сообщение для уведомления в формате \"dd.mm.yyyy MM:HH TEXT\"");
                        logger.error("Processing update Error: wrong format");
                    }catch (NotificationTimeAlreadyPastException e){
                        sendResponceInChat(update.message().chat(), "Дата Уведомления уже прошла");
                        logger.error("Processing update Error: wrong date");
                    }catch (Exception e){
                        sendResponceInChat(update.message().chat(), "Ошибка обработки");
                        logger.error("Processing update Error");
                    }
                }
        );
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void sendResponceInChat(Chat chat, String messageText) {
        SendMessage errorMessage = new SendMessage(chat.id(), messageText);
        telegramBot.execute(errorMessage);
    }

    private void createAndSaveTaskFromMessage(Message message) throws DateTimeParseException, NotificationTimeAlreadyPastException{

        messageHandler.createTaskFromMessage(message);
    }


/*    private boolean isMessageCorrect(String messageText) {
        return messageHandler.checkMessage(messageText);
    }*/

}
