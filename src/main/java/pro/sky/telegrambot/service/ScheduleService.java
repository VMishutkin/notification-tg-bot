package pro.sky.telegrambot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;

@Service
public class ScheduleService {
    @Autowired
    private NotificationTaskRepository notificationTaskRepository;

    @Scheduled(cron = "0/10 * * * * *")
    private void lookForNeededSendNotification(){
        LocalDateTime now = LocalDateTime.now();
        NotificationTask closestTask = notificationTaskRepository.getClosestTask();
        System.out.println(closestTask.getNotificationTaskTime());
    }


}
