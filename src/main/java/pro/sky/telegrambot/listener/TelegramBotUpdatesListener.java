package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    @Autowired
    private NotificationTaskRepository notificationTaskRepository;

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

                        if (messageText.length() > 16) {


                            String stringForCheckingDate = messageText.substring(0, 16);
                            String dateRegex = "([0-9\\.\\:\\s]{16})";
                            Pattern datePattern = Pattern.compile(dateRegex);
                            Matcher matcher = datePattern.matcher(stringForCheckingDate);
                            if (matcher.matches()) {
                                String dateString = matcher.group(0).substring(0, 16);
                                try {
                                    LocalDateTime notificationTime = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                                    //System.out.println(notificationTime.format(DateTimeFormatter.ofPattern("HH:mm , dd MM yyyy ")));
                                    NotificationTask newTask = new NotificationTask(null,
                                            update.message().chat().id(),
                                            messageText.substring(16),
                                            notificationTime);
                                    notificationTaskRepository.save(newTask);
                                    //System.out.println(notificationTaskRepository.findById(2L).orElse(null));
                                    //System.out.println(notificationTaskRepository.save(new NotificationTask(1L, update.message().chat().id(),messageText.substring(16),notificationTime)));
                                } catch (DateTimeParseException e) {
                                    System.out.println("неверно введена дата");
                                }
                            }


                        }


                    }catch (Exception e){

                    }
                }
            );

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
