package crank.us.repositories;

import crank.us.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByTelegramId(Long telegramId);

    boolean deleteByTelegramId(Long telegramId);

    User getByTelegramId(Long telegramId);
}