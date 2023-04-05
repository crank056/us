package crank.us.models;

import crank.us.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TASKS")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "manager_id", referencedColumnName = "id")
    private User manager;
    @ManyToOne
    @JoinColumn(name = "worker_id", referencedColumnName = "id")
    private User worker;
    private String tittle;
    private String text;
    private Integer score;
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;
    @Column(name = "task_status")
    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
        return "Кем выдано: " + this.manager.getFirstName() + " " + this.manager.getLastName() + "\n" +
                "Исполнитель: " + this.worker.getFirstName() + " " + this.worker.getLastName() + "\n" +
                "Дата выдачи: " + this.startDate.format(formatter) + "\n" +
                "Срок выполнения: " + this.endDate.format(formatter) + "\n" +
                "Очки: " + this.score + "\n" +
                "Статус: " + this.taskStatus + "\n" +
                "Текст: " + this.text;
    }
}
