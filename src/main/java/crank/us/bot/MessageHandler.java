package crank.us.bot;

import crank.us.enums.BotMessages;
import crank.us.enums.Division;
import crank.us.enums.Passwords;
import crank.us.exceptions.AccessException;
import crank.us.exceptions.ExistException;
import crank.us.exceptions.WrongFormatException;
import crank.us.locations.PictureLocation;
import crank.us.locations.TaskLocation;
import crank.us.locations.UserLocation;
import crank.us.models.Picture;
import crank.us.models.User;
import crank.us.repositories.UserRepository;
import crank.us.services.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MessageHandler {
    ReplyKeyboardMaker replyKeyboardMaker;
    InlineKeyboardMaker inlineKeyboardMaker;
    UserService userService;
    UserLocation userLocation;
    TaskLocation taskLocation;
    UserRepository userRepository;
    PictureLocation pictureLocation;


    public BotApiMethod<?> answerMessage(Message message) throws ExistException, WrongFormatException, AccessException {
        String inputText = message.getText();
        if (inputText == null) {
            throw new ExistException("Нет айди чата");
        }
        String chatId = message.getChatId().toString();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (!userService.existByTelegramId(Long.parseLong(chatId))) {
            if (!inputText.equalsIgnoreCase("/start") && !inputText.toLowerCase().startsWith("ps")) {
                throw new ExistException("Зарегистрируйтесь отправив боту сообщение по форме: \n" +
                        "пароль табельный номер");
            }
        }
        if (inputText == null) {
            throw new IllegalArgumentException();
        } else if (inputText.toLowerCase().startsWith("/start")) {
            return getStart(message);
        } else if (inputText.toLowerCase().startsWith("ps")) {
            return getRegister(message);
        } else if (inputText.toLowerCase().startsWith("/menu")) {
            return getKeyboard(message);
        } else if (inputText.equalsIgnoreCase("профиль")) {
            return userLocation.getProfile(chatId);
        } else if (inputText.equalsIgnoreCase("работник")) {
            return taskLocation.goToWorker(chatId);
        } else if (inputText.equalsIgnoreCase("руководитель")) {
            return taskLocation.goToManager(chatId);
        } else if (inputText.equalsIgnoreCase("рейтинг")) {
            return userLocation.getRating(chatId);
        } else if (inputText.equalsIgnoreCase("отипб")) {
            return pictureLocation.goToLocation(chatId);
        } else if (inputText.toLowerCase().startsWith("тема")) {
            return taskLocation.setTittle(chatId, inputText);
        } else if (inputText.toLowerCase().startsWith("срок")) {
            return taskLocation.setEnd(chatId, inputText);
        } else if (inputText.toLowerCase().startsWith("задание")) {
            return taskLocation.setText(chatId, inputText);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private SendMessage getStart(Message message) throws ExistException {
        SendMessage sendMessage = new SendMessage(
                message.getChatId().toString(), BotMessages.WELCOME.getMessage());
        return sendMessage;
    }

    private SendMessage getRegister(Message message) throws ExistException, WrongFormatException {
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        for (Division division : Division.values()) {
            buttons.put(division.name(), "USER_SETDIV_" + division.name());
        }
        String link = "";
        String[] split = message.getText().split(" ");
        if (split.length != 2) {
            throw new WrongFormatException("Неверный формат сообщения");
        }
        try {
            Enum.valueOf(Passwords.class, split[0]);
        } catch (IllegalArgumentException e) {
            throw new WrongFormatException("Неверный пароль");
        }
        if (!userRepository.existsByPersonalNumber(Integer.parseInt(split[1]))) {
            throw new ExistException("Вы еще не зарегистрированы в системе. Обратитесь к руководителю");
        }
        userService.createUser(Integer.parseInt(split[1]), message.getFrom().getId());
        String text = "Вы успешно зарегистрировались. Выберите своё подразделение:";
        return inlineKeyboardMaker.makeMessage(message.getChatId().toString(), buttons, text, link);
    }

    private SendMessage getKeyboard(Message message) {
        SendMessage sendMessage = new SendMessage(
                message.getFrom().getId().toString(), "Клавиатура восстановлена");
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());
        return sendMessage;
    }
}
