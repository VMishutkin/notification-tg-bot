package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.NotificationTask;

import java.util.List;

@Repository
public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {

    @Query (value = "SELECT * FROM notification_task WHERE notification_task_time = (SELECT MIN(notification_task_time) FROM notification_task)", nativeQuery = true)
    NotificationTask getClosestTask();

    @Query(value = "SELECT * From notification_task where notification_task_time < now()", nativeQuery = true)
    List<NotificationTask> getPastTasks();

}
