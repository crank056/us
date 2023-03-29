package crank.us.locations;


import crank.us.bot.InlineKeyboardMaker;
import crank.us.services.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserLocation {
    UserService userService;
    InlineKeyboardMaker inlineKeyboardMaker;

    public SendMessage menuHandler(String chatId, String data) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (data.startsWith("USER_SETDIV_")) {
            return setDivision(chatId);
        } else {
            sendMessage.setText("Неизвестная команда, воспользуйся меню");
            return sendMessage;
        }
    }

    private SendMessage setDivision (String chatId) {
        return null;
    }


}
