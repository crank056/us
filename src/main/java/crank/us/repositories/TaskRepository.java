package crank.us.repositories;

import crank.us.enums.Status;
import crank.us.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> getAllByWorkerIdAndStatus(Long workerId, Status status);

    List<Task> getAllByManagerIdAndStatus(Long managerId, Status status);
}
