package crank.us.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

@Component
public class InlineKeyboardMaker {

    public InlineKeyboardMarkup getUniversalInlineMenu(LinkedHashMap<String, String> buttons) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (Map.Entry<String, String> entry : buttons.entrySet()) {
            String name = entry.getKey();
            String callBackData = entry.getValue();
            rowList.add(getButton(name, callBackData));
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getUniversalInlineMenuWithRow(List<LinkedHashMap<String, String>> rows) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (LinkedHashMap<String, String> row : rows) {
            List<InlineKeyboardButton> rowRow = new ArrayList<>();
            for (Map.Entry<String, String> entry : row.entrySet()) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(entry.getKey());
                button.setCallbackData(entry.getValue());
                rowRow.add(button);
            }
            rowList.add(rowRow);
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private List<InlineKeyboardButton> getButton(String buttonName, String buttonCallBackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonName);
        button.setCallbackData(buttonCallBackData);
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(button);
        return keyboardButtonsRow;
    }

    public SendMessage makeMessage(String chatId, LinkedHashMap<String, String> buttons, String text, String link) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.enableMarkdown(true);
        if (!link.isBlank()) {
            sendMessage.setText(text + "LINK" + link);
        } else {
            sendMessage.setText(text);
        }
        sendMessage.setReplyMarkup(getUniversalInlineMenu(buttons));
        return sendMessage;
    }

    public SendMessage makeMessageWithRow(String chatId, List<LinkedHashMap<String,
            String>> rows, String text, String link) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.enableMarkdown(true);
        if (!link.isBlank()) {
            sendMessage.setText(text + "LINK" + link);
        } else {
            sendMessage.setText(text);
        }
        sendMessage.setReplyMarkup(getUniversalInlineMenuWithRow(rows));
        return sendMessage;
    }
}
