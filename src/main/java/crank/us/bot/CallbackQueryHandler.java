package crank.us.bot;

import crank.us.exceptions.AccessException;
import crank.us.exceptions.ExistException;
import crank.us.exceptions.WrongFormatException;
import crank.us.locations.PictureLocation;
import crank.us.locations.TaskLocation;
import crank.us.locations.UserLocation;
import crank.us.services.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CallbackQueryHandler {
    UserService userService;
    UserLocation userLocation;
    TaskLocation taskLocation;
    PictureLocation pictureLocation;

    public BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery)
            throws ExistException, WrongFormatException, AccessException {
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
        }
        if (data.startsWith("TASK")) {
            sendMessage = taskLocation.menuHandler(chatId, data);
            return sendMessage;
        }
        if (data.startsWith("PICTURE")) {
            sendMessage = pictureLocation.menuHandler(chatId, data);
            return sendMessage;
        } else {
            return new SendMessage(chatId, "Неизвестный callback");
        }
    }
}
