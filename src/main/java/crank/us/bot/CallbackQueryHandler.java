package crank.us.bot;

import crank.us.exceptions.ExistException;
import crank.us.exceptions.WrongFormatException;
import crank.us.services.UserService;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public class CallbackQueryHandler {
    UserService userService;

    public BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery)
            throws ExistException, WrongFormatException {
        final String chatId = buttonQuery.getMessage().getChatId().toString();
        String data = buttonQuery.getData();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (!userService.existByTelegramId(Long.parseLong(chatId))) {
            throw new ExistException("Сначала нужно зарегистрироваться");
        }
        if (data.contains("HOME")) {
            sendMessage = home.menuHandler(chatId, data);
            return sendMessage;
        } else if (data.contains("SETTING")) {
            sendMessage = setting.menuHandler(chatId, data);
            return sendMessage;
        } else if (data.contains("MARKET")) {

            sendMessage = market.menuHandler(chatId, data);
            return sendMessage;
        } else if (data.contains("FACTORY")) {
            checkNick(chatId);
            sendMessage = factory.menuHandler(chatId, data);
            return sendMessage;
        } else if (data.contains("ARENA")) {
            checkNick(chatId);
            sendMessage = arena.menuHandler(chatId, data);
            return sendMessage;
        } else if (data.contains("POST")) {
            checkNick(chatId);
            sendMessage = post.menuHandler(chatId, data);
            return sendMessage;
        } else if (data.contains("RATING")) {
            checkNick(chatId);
            sendMessage = rating.menuHandler(chatId, data);
            return sendMessage;
        } else if (data.startsWith("GUILD")) {
            checkNick(chatId);
            sendMessage = guild.menuHandler(chatId, data);
            return sendMessage;
        } else {
            return new SendMessage(chatId, "Неизвестный callback");
        }
    }
}
