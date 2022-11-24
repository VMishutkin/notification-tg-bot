package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleService {

    private final NotificationTaskRepository notificationTaskRepository;


    private final TelegramBot telegramBot;

    public ScheduleService(NotificationTaskRepository notificationTaskRepository, TelegramBot telegramBot) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.telegramBot = telegramBot;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    private void checkPastTasks() {
            List<NotificationTask> pastTask = notificationTaskRepository.getPastTasks();
            if(pastTask==null){
                return;
            }

        for (NotificationTask task: pastTask) {
            sendNotificationResponse(task);
            deleteEntryFromBase(task);
        }
    }


    private void checkTimeForClosestNotifications() {
        boolean isNeedCheckNext;
        do {
            isNeedCheckNext = false;
            NotificationTask closestTask = notificationTaskRepository.getClosestTask();
            if (closestTask == null) {
                break;
            }

            if (isNotificationTimePast(closestTask.getNotificationTaskTime())) {
                sendNotificationResponse(closestTask);
                deleteEntryFromBase(closestTask);
                isNeedCheckNext = true;
            }

        } while (isNeedCheckNext);

    }

    private boolean isNotificationTimePast(LocalDateTime notificationTaskTime) {
        LocalDateTime now = LocalDateTime.now();
        return notificationTaskTime.isBefore(now);
    }

    private void deleteEntryFromBase(NotificationTask task) {
        notificationTaskRepository.delete(task);
    }

    private void sendNotificationResponse(NotificationTask task) {
        String messageText = task.getNotificationText();
        Long chatId = task.getChatID();
        SendMessage message = new SendMessage(chatId, messageText);
        telegramBot.execute(message);

    }
}