package crank.us.models.user;

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
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Override
    public String toString() {
        return "Telegram id: " + this.telegramId +
                "Firstname: " + this.firstName +
                "Lastname: " + this.lastName +
                "Reg date: " + this.registrationDate;
    }
}
