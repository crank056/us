package crank.us.bot;

import crank.us.exceptions.ExistException;
import crank.us.exceptions.WrongFormatException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Slf4j
public class Bot extends SpringWebhookBot {
    String botPath;
    String botUsername;
    String botToken;
    MessageHandler messageHandler;
    CallbackQueryHandler callbackQueryHandler;


    public Bot(SetWebhook setWebhook, MessageHandler messageHandler, CallbackQueryHandler callbackQueryHandler) {
        super(setWebhook);
        this.messageHandler = messageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
        setBotToken(botToken);
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        String chatId;
        if (update.getMessage() != null) {
            if (update.getMessage().hasText()) {
                if (update.getMessage().getText().length() > 1999) {
                    try {
                        throw new WrongFormatException("Слишком длинное сообщение");
                    } catch (WrongFormatException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            chatId = update.getMessage().getChatId().toString();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        } else {
            chatId = null;
        }
        if (chatId != null) {
            try {
                SendMessage sendMessage = (SendMessage) handleUpdate(update);
                if (sendMessage != null) {
                    if (sendMessage.getText().contains("LINK")) {
                        log.info("Link");
                        String[] split = sendMessage.getText().split("LINK");
                        String link = split[split.length - 1];
                        sendMessage.setText(split[0]);
                        execute(new SendPhoto(sendMessage.getChatId(), new InputFile(new File(link))));

                    }
                    sendMessage.setText(sendMessage.getText().replace("_", "\\_"));
                } else {
                    throw new ExistException("Непредвиденная ошибка");
                }
                return sendMessage;
            } catch (IllegalArgumentException e) {
                return new SendMessage(chatId,
                        "Недоступный вид сообщения");
            } catch (ExistException | WrongFormatException e) {
                return new SendMessage(chatId, e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

    private BotApiMethod<?> handleUpdate(Update update) throws ExistException, WrongFormatException {
        if (update.hasCallbackQuery()) {
            log.info("has СallBackQuerry: {}", update.getCallbackQuery().getData());
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return callbackQueryHandler.processCallbackQuery(callbackQuery);
        } else {
            Message message = update.getMessage();
            if (message != null) {
                log.info("has message: {}", message.getText());
                return messageHandler.answerMessage(update.getMessage());
            }
        }
        return null;
    }
}

