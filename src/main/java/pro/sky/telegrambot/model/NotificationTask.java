package pro.sky.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class NotificationTask {
    @Id
    @GeneratedValue
    private Long id;
    private Long chatID;
    private String notificationText;
    private LocalDateTime notificationTaskTime;

    public NotificationTask() {
    }

    public NotificationTask(
                            Long chatID,
                            String notificationText,
                            LocalDateTime notificationTaskTime) {

        this.chatID = chatID;
        this.notificationText = notificationText;
        this.notificationTaskTime = notificationTaskTime;
    }

    public Long getId() {
        return id;
    }

    public Long getChatID() {
        return chatID;
    }

    public String getNotificationText() {
        if(notificationText==null)
            return " ";
        return notificationText;
    }

    public LocalDateTime getNotificationTaskTime() {
        return notificationTaskTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setChatID(Long chatID) {
        this.chatID = chatID;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public void setNotificationTaskTime(LocalDateTime notificationTaskTime) {
        this.notificationTaskTime = notificationTaskTime;
    }

    @Override
    public String toString() {
        return String.format("id = %d, ChatID = %d, text = %s, Time = %4$tM:%4$tH, Date = %4$td %4$tB %4$ty",
                id, chatID, notificationText,notificationTaskTime);
    }
}
