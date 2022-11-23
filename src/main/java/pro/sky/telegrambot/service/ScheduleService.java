package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;

@Service
public class ScheduleService {
    @Autowired
    private NotificationTaskRepository notificationTaskRepository;
    @Autowired
    private TelegramBot telegramBot;

    @Scheduled(cron = "0 0/1 * * * *")
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