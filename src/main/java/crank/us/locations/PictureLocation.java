package crank.us.locations;

import crank.us.bot.InlineKeyboardMaker;
import crank.us.exceptions.AccessException;
import crank.us.exceptions.ExistException;
import crank.us.models.Picture;
import crank.us.repositories.PictureRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class PictureLocation {
    PictureRepository pictureRepository;
    InlineKeyboardMaker inlineKeyboardMaker;

    public SendMessage goToLocation(String chatId) {
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        Sort sortByActual = Sort.by(Sort.Direction.DESC, "actual");
        Pageable page = PageRequest.of(0, 10, sortByActual);
        List<Picture> pictureList = pictureRepository.findAll(page).getContent();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for(Picture picture: pictureList) {
            if(picture.getActual()) {
                buttons.put(picture.getAddDate().format(formatter) + ": " + picture.getTittle(), "PICTURE_GET_" + picture.getId());
            }
        }
        String link = "";
        return inlineKeyboardMaker.makeMessage(chatId, buttons, "Информационные сообщения", link);
    }

    public SendMessage menuHandler(String chatId, String data) throws AccessException, ExistException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (data.startsWith("PICTURE_GET_")) {
            return getPic(chatId, data);
        } else {
            sendMessage.setText("Неизвестная команда, воспользуйся меню");
            return sendMessage;
        }
    }

    private SendMessage getPic(String chatId, String data) {
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        String[] split = data.split("_");
        Picture picture = pictureRepository.getReferenceById(Long.parseLong(split[2]));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String text = picture.getAddDate().format(formatter) + ": " + picture.getTittle();
        return inlineKeyboardMaker.makeMessage(chatId, buttons, text, picture.getLink());
    }
}
