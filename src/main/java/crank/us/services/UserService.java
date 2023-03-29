package crank.us.services;

import crank.us.exceptions.ExistException;
import crank.us.models.User;
import crank.us.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class UserService {
    UserRepository userRepository;

    @Transactional
    public User createUser(User user) throws ExistException {
        if (userRepository.existsByTelegramId(user.getTelegramId())) {
            throw new ExistException("Вы уже регистрировались");
        }
        return userRepository.save(user);
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
