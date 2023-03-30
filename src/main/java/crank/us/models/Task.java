package crank.us.models;

import crank.us.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    @Enumerated(EnumType.STRING)
    private Status status;

    @Override
    public String toString() {
        return "Кем выдано: " + this.manager.getFirstName() + " " + this.manager.getLastName() + "\n" +
                "Исполнитель: " + this.worker.getFirstName() + " " + this.worker.getLastName() + "\n" +
                "Дата выдачи: " + this.startDate + "\n" +
                "Срок выполнения: " + this.endDate + "\n" +
                "Очки: " + this.score + "\n" +
                "Статус: " + this.status + "\n" +
                "Текст: " + this.text;
    }
}
