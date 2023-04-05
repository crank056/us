package crank.us.repositories;

import crank.us.enums.TaskStatus;
import crank.us.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> getAllByWorkerIdAndTaskStatus(Long workerId, TaskStatus taskStatus);

    List<Task> getAllByManagerIdAndTaskStatus(Long managerId, TaskStatus taskStatus);
}
