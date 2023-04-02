package crank.us.locations;


import crank.us.bot.InlineKeyboardMaker;
import crank.us.bot.ReplyKeyboardMaker;
import crank.us.enums.Division;
import crank.us.enums.Status;
import crank.us.models.User;
import crank.us.repositories.TaskRepository;
import crank.us.repositories.UserRepository;
import crank.us.services.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
        } else {
            sendMessage.setText("Неизвестная команда, воспользуйся меню");
            return sendMessage;
        }
    }

    public SendMessage getProfile(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        User user = userService.getUserByTelegramId(Long.parseLong(chatId));
        String text = user.toString();
        text = text + "Новых задач: "
                + taskRepository.getAllByWorkerIdAndStatus(user.getId(), Status.НОВАЯ).size() + "\n";
        text = text + "Задач в процессе: "
                + taskRepository.getAllByWorkerIdAndStatus(user.getId(), Status.ВЫПОЛНЯЕТСЯ).size() + "\n";
        text = text + "Завершенных задач: "
                + taskRepository.getAllByWorkerIdAndStatus(user.getId(), Status.ЗАВЕРШЕНА).size() + "\n";
        sendMessage.setText(text);
        return sendMessage;
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
}
