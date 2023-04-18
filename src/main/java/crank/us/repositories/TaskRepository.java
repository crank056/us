package crank.us.repositories;

import crank.us.enums.TaskStatus;
import crank.us.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> getAllByWorkerIdAndTaskStatus(Long workerId, TaskStatus taskStatus);

    List<Task> getAllByManagerIdAndTaskStatus(Long managerId, TaskStatus taskStatus);

    List<Task> getAllByManagerIdAndEndDateBefore(Long managerId, LocalDateTime endDate);

    List getAllByTaskStatus(TaskStatus taskStatus);

    List<Task> getAllByRepeatAndTaskStatus(Boolean isRepeat, TaskStatus taskStatus);
}
