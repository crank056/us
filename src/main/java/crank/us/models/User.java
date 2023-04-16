package crank.us.models;

import crank.us.enums.Division;
import crank.us.enums.Position;
import crank.us.enums.UserStatus;
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
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "telegram_id")
    private Long telegramId;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "personal_number")
    private Integer personalNumber;
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;
    @ManyToOne
    @JoinColumn(name = "manager_id", referencedColumnName = "id")
    private User manager;
    @Column(name = "is_manager")
    private Boolean isManager;
    @Enumerated(EnumType.STRING)
    private Division division;
    @ManyToOne
    @JoinColumn(name = "worker_id", referencedColumnName = "id")
    private User workerForTask;
    @Column(name = "tittle_for_task")
    private String tittleForTask;
    @Column(name = "end_of_task")
    private LocalDateTime endOfTask;
    @Column(name = "task_text")
    private String taskText;
    @Column(name = "task_score")
    private Integer taskScore;
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    @Enumerated(EnumType.STRING)
    private Position position;
    private Long rating;

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return "Имя: " + this.firstName + "\n" +
                "Фамилия: " + this.lastName + "\n" +
                "Дата регистрации: " + this.registrationDate.format(formatter) + "\n" +
                "Табельный номер: " + this.personalNumber + "\n" +
                "Должность: " + this.position + "\n" +
                "Подразделение: " + this.division + "\n" +
                "Руководитель: " + this.manager.getFirstName() + " " + this.manager.getLastName() + "\n" +
                "Статус: " + this.status + "\n" +
                "Рейтинг: " + this.rating + "\n";
    }
}
