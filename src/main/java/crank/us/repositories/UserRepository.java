package crank.us.repositories;

import crank.us.enums.Division;
import crank.us.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByTelegramId(Long telegramId);

    boolean deleteByTelegramId(Long telegramId);

    User getByTelegramId(Long telegramId);

    List<User> getAllByDivisionAndIsManager(Division division, boolean isManager);

    List<User> getAllByManagerId(Long managerId);
}