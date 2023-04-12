package crank.us.locations;


import crank.us.bot.InlineKeyboardMaker;
import crank.us.bot.ReplyKeyboardMaker;
import crank.us.enums.Division;
import crank.us.enums.TaskStatus;
import crank.us.enums.UserStatus;
import crank.us.models.Task;
import crank.us.models.User;
import crank.us.repositories.TaskRepository;
import crank.us.repositories.UserRepository;
import crank.us.services.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.transaction.Transactional;
import java.util.LinkedHashMap;
import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class UserLocation {
    UserService userService;
    InlineKeyboardMaker inlineKeyboardMaker;
    UserRepository userRepository;
    TaskRepository taskRepository;

    ReplyKeyboardMaker replyKeyboardMaker;

    public SendMessage menuHandler(String chatId, String data) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (data.startsWith("USER_SETDIV_")) {
            return setDivision(chatId, data);
        } else if (data.startsWith("USER_SETMANAGER_")) {
            return setManager(chatId, data);
        } else if (data.startsWith("USER_GETSTATUSLIST")) {
            return getStatusList(chatId);
        } else if (data.startsWith("USER_SETSTATUS_")) {
            return setStatus(chatId, data);
        }else {
            sendMessage.setText("Неизвестная команда, воспользуйся меню");
            return sendMessage;
        }
    }

    public SendMessage getProfile(String chatId) {
        User user = userService.getUserByTelegramId(Long.parseLong(chatId));
        String text = user.toString();
        text = text + "Новых задач: "
                + taskRepository.getAllByWorkerIdAndTaskStatus(user.getId(), TaskStatus.НОВАЯ).size() + "\n";
        text = text + "Задач в процессе: "
                + taskRepository.getAllByWorkerIdAndTaskStatus(user.getId(), TaskStatus.ВЫПОЛНЯЕТСЯ).size() + "\n";
        text = text + "Завершенных задач: "
                + taskRepository.getAllByWorkerIdAndTaskStatus(user.getId(), TaskStatus.ЗАВЕРШЕНА).size() + "\n";
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        String link = "";
        buttons.put("Сменить статус", "USER_GETSTATUSLIST");
        return inlineKeyboardMaker.makeMessage(chatId, buttons, text, link);
    }

    private SendMessage setDivision(String chatId, String data) {
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        String link = "";
        User user = userService.getUserByTelegramId(Long.parseLong(chatId));
        String[] split = data.split("_");
        List<User> managers = userRepository.getAllByDivisionAndIsManager(Division.valueOf(split[2]), true);
        String text = "Выберите непосредственного руководителя";
        for (User manager : managers) {
            buttons.put(
                    manager.getFirstName() + " " + manager.getLastName(),
                    "USER_SETMANAGER_" + user.getId() + "_" + manager.getId());
        }
        user.setDivision(Division.valueOf(split[2]));
        return inlineKeyboardMaker.makeMessage(chatId, buttons, text, link);
    }

    private SendMessage setManager(String chatId, String data) {
        User user = userService.getUserByTelegramId(Long.parseLong(chatId));
        String[] split = data.split("_");
        user.setManager(userRepository.getReferenceById(Long.parseLong(split[3])));
        SendMessage sendMessage = new SendMessage(
                chatId, "Успешная регистрация!");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());
        return sendMessage;
    }

    private SendMessage getStatusList(String chatId) {
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        String link = "";
        String text = "Выберите статус";
        for(UserStatus status: UserStatus.values()) {
            buttons.put(status.name(), "USER_SETSTATUS_" + status);
        }
        return inlineKeyboardMaker.makeMessage(chatId, buttons, text, link);
    }

    private SendMessage setStatus(String chatId, String data) {
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        String[] split = data.split("_");
        User user = userService.getUserByTelegramId(Long.parseLong(chatId));
        user.setStatus(UserStatus.valueOf(split[2]));
        String text = "Статус установлен на " + user.getStatus();
        String link = "";
        return inlineKeyboardMaker.makeMessage(chatId, buttons, text, link);
    }

    public SendMessage getRating(String chatId) {
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        Sort sortByRating = Sort.by(Sort.Direction.DESC, "rating");
        Pageable page = PageRequest.of(0, 10, sortByRating);
        List<User> rating = userRepository.findAllByDivision(page, userRepository.getByTelegramId(Long.parseLong(chatId)).getDivision()).getContent();
        int count = 1;
        String text = "";
        for(User user: rating) {
            if(user.getRating() != null) {
                text = text + count + ". " + user.getFirstName() + " " + user.getLastName() + ": " + user.getRating() + "\n";
                count++;
            }
        }
        count++;
        String link = "";
        return inlineKeyboardMaker.makeMessage(chatId, buttons, text, link);
    }
}
