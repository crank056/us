package crank.us.services;

import crank.us.exceptions.ExistException;
import crank.us.models.User;
import crank.us.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class UserService {
    UserRepository userRepository;

    @Transactional
    public User createUser(Integer personalNumber, Long telegramId) throws ExistException {
        User user = userRepository.getByPersonalNumber(personalNumber);
        if(user.getTelegramId() != null) {
            throw new ExistException("Вы уже регистрировались");
        }
        user.setTelegramId(telegramId);
        user.setRegistrationDate(LocalDateTime.now());
        return user;
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.getReferenceById(id);
    }

    public boolean existByTelegramId(Long telegramId) {
        return userRepository.existsByTelegramId(telegramId);
    }

    public User getUserByTelegramId(Long telegramId) {
        return userRepository.getByTelegramId(telegramId);
    }
}
