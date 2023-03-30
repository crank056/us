package crank.us.bot;

import crank.us.enums.BotMessages;
import crank.us.enums.Division;
import crank.us.enums.Passwords;
import crank.us.exceptions.ExistException;
import crank.us.exceptions.WrongFormatException;
import crank.us.locations.TaskLocation;
import crank.us.locations.UserLocation;
import crank.us.models.User;
import crank.us.services.UserService;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

public class MessageHandler {
    ReplyKeyboardMaker replyKeyboardMaker;
    InlineKeyboardMaker inlineKeyboardMaker;
    UserService userService;
    UserLocation userLocation;
    TaskLocation taskLocation;


    public BotApiMethod<?> answerMessage(Message message) throws ExistException, WrongFormatException {
        String inputText = message.getText();
        if (inputText == null) {
            throw new ExistException("Нет айди чата");
        }
        String chatId = message.getChatId().toString();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (!userService.existByTelegramId(Long.parseLong(chatId))) {
            if (!inputText.equalsIgnoreCase("/start")) {
                throw new ExistException("Зарегистрируйтесь через комманду /reg");
            }
        }
        if (inputText == null) {
            throw new IllegalArgumentException();
        } else if (inputText.toLowerCase().startsWith("/start")) {
            return getStart(message);
        } else if (inputText.toLowerCase().startsWith("/reg")) {
            return getRegister(message);
        } else if (inputText.toLowerCase().startsWith("/keyboard")) {
            return getKeyboard(message);
        } else if (inputText.equalsIgnoreCase("профиль")) {
            return userLocation.getProfile(chatId);
        } else if (inputText.equalsIgnoreCase("задания")) {
            return taskLocation.goToLocation(chatId);
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
        for(Division division: Division.values()) {
            buttons.put(division.name(), "USER_SETDIV_" + division.name());
        }
        String link = "";
        String[] split = message.getText().split(" ");
        if(split.length < 5) {
            throw new WrongFormatException("Неверный формат сообщения");
        }
        try {
            Enum.valueOf(Passwords.class, split[1]);
        } catch (IllegalArgumentException e) {
            throw new WrongFormatException("Неверный пароль");
        }
        User user = new User(null,
                message.getFrom().getId(),
                split[2], split[3], Integer.parseInt(split[4]),
                LocalDateTime.now(), null, null, null);
        userService.createUser(user);
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
