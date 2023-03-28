package crank.us.bot;

import crank.us.enums.BotMessages;
import crank.us.exceptions.ExistException;
import crank.us.models.user.User;
import crank.us.services.UserService;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;

public class MessageHandler {
    ReplyKeyboardMaker replyKeyboardMaker;
    UserService userService;


    public BotApiMethod<?> answerMessage(Message message) throws ExistException {
        String inputText = message.getText();
        if (inputText == null) {
            throw new ExistException("Нет айди чата");
        }
        String chatId = message.getChatId().toString();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (!userService.existByTelegramId(Long.parseLong(chatId))) {
            if (!inputText.equalsIgnoreCase("/start")) {
                throw new ExistException("Зарегистрируйтесь через комманду /start");
            }
        }
        if (inputText == null) {
            throw new IllegalArgumentException();
        } else if (inputText.equalsIgnoreCase("/start")) {
            sendMessage.setText(BotMessages.WELCOME.getMessage());
            getRegister(message);
            return sendMessage;
        } else if (inputText.toLowerCase().startsWith("/help")) {
            return setting.getHelpMenu(chatId);
        } else if (inputText.toLowerCase().startsWith("/keyboard")) {
            return setting.getKeyboard(message.getChatId().toString());
        } else if (heroService.getHeroByTelegramId(Long.parseLong(chatId)).getNickname() == null) {
            heroService.setNickname(Long.parseLong(chatId), message.getText());
            sendMessage.setText("Твой новый ник: " + message.getText()
                    + "\nДля просмотра тура по игре используй команду /help");
            sendMessage.enableMarkdown(true);
            sendMessage.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());
            return sendMessage;
        } else if (inputText.toLowerCase().startsWith("ник")) {
            return setting.setNickname(message);
        } else if (inputText.toLowerCase().startsWith("ошибка")) {
            return setting.report(message);
        } else if (inputText.toLowerCase().startsWith("мой друг ")) {
            return setting.setFriend(chatId, inputText);
        } else if (inputText.equalsIgnoreCase("Персонаж")) {
            checkNick(chatId);
            return home.goToLocation(chatId);
        } else if (inputText.equalsIgnoreCase("Магазин")) {
            checkNick(chatId);
            return market.goToLocation(chatId);
        } else if (inputText.equalsIgnoreCase("Арена")) {
            checkNick(chatId);
            return arena.goToLocation(chatId);
        } else if (inputText.equalsIgnoreCase("Завод")) {
            checkNick(chatId);
            return factory.goToLocation(chatId);
        } else if (inputText.equalsIgnoreCase("Почта")) {
            checkNick(chatId);
            return post.goToLocation(chatId);
        } else if (inputText.equalsIgnoreCase("Рейтинг")) {
            checkNick(chatId);
            return rating.goToLocation(chatId);
        } else if (inputText.equalsIgnoreCase("Настройки")) {
            return setting.goToLocation(chatId);
        } else if (inputText.equalsIgnoreCase("Гильдии")) {
            return guild.goToLocation(chatId);
        } else if (inputText.startsWith("58355835 создай босса ")) {
            String[] split = inputText.split(" ");
            bossService.createBoss(split[3], Long.parseLong(split[4]), Long.parseLong(split[5]));
            sendMessage.setText("Создан");
            return sendMessage;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private SendMessage getRegister(Message message) throws ExistException {
        User user = new User(null,
                message.getFrom().getId(),
                message.getFrom().getFirstName(),
                message.getFrom().getLastName(),
                LocalDateTime.now());
        user = userService.createUser(user);
        SendMessage sendMessage = new SendMessage(
                user.getTelegramId().toString(), BotMessages.WELCOME.getMessage());
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());
        return sendMessage;
    }
}
