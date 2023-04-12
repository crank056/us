package crank.us.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReplyKeyboardMaker {

    public ReplyKeyboardMarkup getMainMenuKeyboard() {
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(createKeyBoardRow("Профиль"));
        keyboard.add(createKeyBoardRow("Задания"));
        keyboard.add(createKeyBoardRow("Рейтинг"));
        keyboard.add(createKeyBoardRow("Настройки"));
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        return replyKeyboardMarkup;
    }


    private KeyboardRow createKeyBoardRow(String text) {
        String[] split = text.split(" ");
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(split[0]));
        if (split.length > 1) {
            row.add(new KeyboardButton(split[1]));
        }
        return row;
    }
}
