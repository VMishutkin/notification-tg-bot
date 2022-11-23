package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.MessageHandler;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {


    @Autowired
    private MessageHandler messageHandler;

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
                    logger.info("Processing update: {}", update);
                    try {
                        String messageText = update.message().text();
                        if (isMessageCorrect(messageText)) {
                            createAndSaveTaskFromMessage(update.message());
                            sendAcceptInChat(update.message().chat());
                        } else {
                            sendErrorInChat(update.message().chat());
                        }
                    } catch (Exception e) {
                        logger.error("Processing update Error");
                    }
                }
        );
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void sendAcceptInChat(Chat chat) {
        String message = "Напоминание создано";
        SendMessage errorMessage = new SendMessage(chat.id(), message);
        telegramBot.execute(errorMessage);
    }

    private void createAndSaveTaskFromMessage(Message message) {
        messageHandler.createTaskFromMessage(message);
    }

    private void sendErrorInChat(Chat chat) {
        String message = "Введите сообщение для уведомления в формате \"dd.mm.yyyy MM:HH TEXT\"";
        SendMessage errorMessage = new SendMessage(chat.id(), message);
        telegramBot.execute(errorMessage);
    }

    private boolean isMessageCorrect(String messageText) {
        return messageHandler.checkMessage(messageText);
    }

}
