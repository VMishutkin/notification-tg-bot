package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exceptions.NotificationTimeAlreadyPastException;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MessageHandler {


    private String dateRegex = "([0-9\\.\\:\\s]{16})";
    private Pattern datePattern = Pattern.compile(dateRegex);


    private final NotificationTaskRepository notificationTaskRepository;

    public MessageHandler(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

//    public boolean checkMessage(String messageText) {
//        if (messageText.length() < 16) {
//            return false;
//        }
//        String dateString = messageText.substring(0, 16);
//        LocalDateTime notificationTime = parseTime(dateString);
//        if (notificationTime == null || notificationTime.isBefore(LocalDateTime.now())) {
//            return false;
//        }
//        return true;
//    }

    public LocalDateTime parseTime(String stringForCheckingDate) throws DateTimeParseException, NotificationTimeAlreadyPastException {

        Matcher matcher = datePattern.matcher(stringForCheckingDate);
        LocalDateTime notificationTime = null;
        if (matcher.matches()) {
            String dateString = matcher.group(0).substring(0, 16);
            notificationTime = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            if (notificationTime.isBefore(LocalDateTime.now())){
                throw new NotificationTimeAlreadyPastException();
            }

        }
        return notificationTime;
    }

    public void createTaskFromMessage(Message message) throws DateTimeParseException, NotificationTimeAlreadyPastException {
        String messageText = message.text();
                if (messageText.length() < 16) {
           throw new DateTimeParseException("Wrong input data", null,0);
        }
        String dateString = message.text().substring(0, 16);
        LocalDateTime taskTime = parseTime(dateString);

        String taskText;
        if (message.text().length() > 17) {
            taskText = message.text().substring(17);
        } else {
            taskText = "Уведомление";
        }
        Long chatid = message.chat().id();
        NotificationTask newTask = new NotificationTask(chatid, taskText, taskTime);
        notificationTaskRepository.save(newTask);
    }
}




