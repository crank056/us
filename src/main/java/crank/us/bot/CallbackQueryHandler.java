package crank.us.bot;

import crank.us.exceptions.ExistException;
import crank.us.exceptions.WrongFormatException;
import crank.us.locations.UserLocation;
import crank.us.services.UserService;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public class CallbackQueryHandler {
    UserService userService;
    UserLocation userLocation;

    public BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery)
            throws ExistException, WrongFormatException {
        final String chatId = buttonQuery.getMessage().getChatId().toString();
        String data = buttonQuery.getData();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (!userService.existByTelegramId(Long.parseLong(chatId))) {
            throw new ExistException("Сначала нужно зарегистрироваться");
        }
        if (data.startsWith("USER")) {
            sendMessage = userLocation.menuHandler(chatId, data);
            return sendMessage;
        } else {
            return new SendMessage(chatId, "Неизвестный callback");
        }
    }
}
