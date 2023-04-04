package crank.us.locations;

import crank.us.bot.InlineKeyboardMaker;
import crank.us.enums.Status;
import crank.us.exceptions.AccessException;
import crank.us.models.Task;
import crank.us.models.User;
import crank.us.repositories.TaskRepository;
import crank.us.repositories.UserRepository;
import crank.us.services.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class TaskLocation {
    UserRepository userRepository;
    UserService userService;
    TaskRepository taskRepository;
    InlineKeyboardMaker inlineKeyboardMaker;

    public SendMessage goToLocation(String chatId) {
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        buttons.put("Новые задания", "TASK_GETLIST_НОВАЯ");
        buttons.put("Текущие задания", "TASK_GETLIST_ВЫПОЛНЯЕТСЯ");
        buttons.put("Завершенные задания", "TASK_GETLIST_ЗАВЕРШЕНА");
        buttons.put("Согласованные задания", "TASK_GETLIST_СОГЛАСОВАНА");
        buttons.put("Выданные задания", "TASK_GIVED");
        buttons.put("Выдать задание", "TASK_GIVE");
        String link = "";
        return inlineKeyboardMaker.makeMessage(chatId, buttons, "Меню заданий", link);
    }

    public SendMessage menuHandler(String chatId, String data) throws AccessException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (data.startsWith("TASK_GETLIST_")) {
            return getList(chatId, data);
        } else if (data.startsWith("TASK_GET_")) {
            return getTask(chatId, data);
        } else if (data.startsWith("TASK_SETSTATUS_")) {
            return setStatus(chatId, data);
        } else if (data.startsWith("TASK_GIVED")) {
            return getGivedTasks(chatId);
        } else if (data.startsWith("TASK_GETGIVELIST_")) {
            return getGivedTasksList(chatId, data);
        } else if (data.equals("TASK_GIVE")) {
            return getWorkerList(chatId);
        } else if (data.startsWith("TASK_GIVE_")) {
            return giveTask(chatId, data);
        } else if (data.startsWith("TASK_SETSCORE_")) {
            return setScore(chatId, data);
        } else {
            sendMessage.setText("Неизвестная команда, воспользуйся меню");
            return sendMessage;
        }
    }

    private SendMessage getList(String chatId, String data) {
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        String[] split = data.split("_");
        List<Task> taskList = taskRepository.getAllByWorkerIdAndStatus(
                userService.getUserByTelegramId(Long.parseLong(chatId)).getId(),
                Status.valueOf(split[2]));
        for (Task task : taskList) {
            buttons.put(task.getTittle(), "TASK_GET_" + task.getId());
        }
        String link = "";
        return inlineKeyboardMaker.makeMessage(chatId, buttons, "Список задач со статусом " + split[2], link);
    }

    private SendMessage getTask(String chatId, String data) throws AccessException {
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        String[] split = data.split("_");
        Task task = taskRepository.getReferenceById(Long.parseLong(split[2]));
        if (!task.getManager().getTelegramId().equals(Long.parseLong(chatId)) && !task.getWorker().getTelegramId().equals(Long.parseLong(chatId))) {
            throw new AccessException("У вас нет доступа к этой задаче");
        }
        String text = task.toString();
        if (task.getWorker().getTelegramId().equals(Long.parseLong(chatId))) {
            if (task.getStatus().equals(Status.НОВАЯ)) {
                buttons.put("Принять к исполнению", "TASK_SETSTATUS_" + Status.ВЫПОЛНЯЕТСЯ + "_" + task.getId());
            } else if (task.getStatus().equals(Status.ВЫПОЛНЯЕТСЯ)) {
                buttons.put("Завершить", "TASK_SETSTATUS_" + Status.ЗАВЕРШЕНА + "_" + task.getId());
            }
        }
        if (task.getManager().getTelegramId().equals(Long.parseLong(chatId))) {
            if (task.getStatus().equals(Status.НОВАЯ)
                    || task.getStatus().equals(Status.ВЫПОЛНЯЕТСЯ)) {
                buttons.put("Отменить", "TASK_SETSTATUS_" + Status.ОТМЕНЕНА + "_" + task.getId());
            } else if (task.getStatus().equals(Status.ЗАВЕРШЕНА)) {
                buttons.put("Согласовать", "TASK_SETSTATUS_" + Status.СОГЛАСОВАНА + "_" + task.getId());
            }
        }
        buttons.put("К списку текущих задач", "TASK_GETLIST_ВЫПОЛНЯЕТСЯ");
        String link = "";
        return inlineKeyboardMaker.makeMessage(chatId, buttons, text, link);
    }

    private SendMessage setStatus(String chatId, String data) {
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        String[] split = data.split("_");
        Task task = taskRepository.getReferenceById(Long.parseLong(split[3]));
        task.setStatus(Status.valueOf(split[2]));
        String text = "Статус установлен на " + task.getStatus();
        buttons.put("К списку текущих задач", "TASK_GETLIST_ВЫПОЛНЯЕТСЯ");
        String link = "";
        return inlineKeyboardMaker.makeMessage(chatId, buttons, text, link);
    }

    private SendMessage getGivedTasks(String chatId) throws AccessException {
        if (!userService.getUserByTelegramId(Long.parseLong(chatId)).getIsManager()) {
            throw new AccessException("Вы не являетесь руководителем");
        }
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        buttons.put("Новые задания", "TASK_GETGIVELIST_НОВАЯ");
        buttons.put("Текущие задания", "TASK_GETGIVELIST_ВЫПОЛНЯЕТСЯ");
        buttons.put("Завершенные задания", "TASK_GETGIVELIST_ЗАВЕРШЕНА");
        buttons.put("Согласованные задания", "TASK_GETGIVELIST_СОГЛАСОВАНА");
        String link = "";
        return inlineKeyboardMaker.makeMessage(chatId, buttons, "Статусы выданных заданий", link);
    }

    private SendMessage getGivedTasksList(String chatId, String data) {
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        String[] split = data.split("_");
        List<Task> taskList = taskRepository.getAllByManagerIdAndStatus(
                userService.getUserByTelegramId(Long.parseLong(chatId)).getId(),
                Status.valueOf(split[2]));
        for (Task task : taskList) {
            buttons.put(task.getTittle(), "TASK_GET_" + task.getId());
        }
        String link = "";
        return inlineKeyboardMaker.makeMessage(chatId, buttons, "Список задач со статусом " + split[2], link);
    }

    private SendMessage getWorkerList(String chatId) throws AccessException {
        if (!userService.getUserByTelegramId(Long.parseLong(chatId)).getIsManager()) {
            throw new AccessException("Вы не являетесь руководителем");
        }
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        List<User> workerList = userRepository.getAllByManagerId(userService.getUserByTelegramId(Long.parseLong(chatId)).getId());
        for (User user : workerList) {
            buttons.put(user.getFirstName() + " " + user.getLastName(), "TASK_GIVE_" + user.getId());
        }
        String link = "";
        return inlineKeyboardMaker.makeMessage(chatId, buttons, "Выберите подчиненного", link);
    }

    private SendMessage giveTask(String chatId, String data) throws AccessException {
        if (!userService.getUserByTelegramId(Long.parseLong(chatId)).getIsManager()) {
            throw new AccessException("Вы не являетесь руководителем");
        }
        String[] split = data.split("_");
        User manager = userService.getUserByTelegramId(Long.parseLong(chatId));
        manager.setWorkerForTask(userRepository.getReferenceById(Long.parseLong(split[2])));
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        buttons.put("К списку подчиненных", "TASK_GIVE");
        String link = "";
        String text = "Введите тему задания в формате:\n" +
                "Тема текст темы";
        return inlineKeyboardMaker.makeMessage(chatId, buttons, text, link);
    }

    public SendMessage setTittle(String chatId, String data) {
        User manager = userService.getUserByTelegramId(Long.parseLong(chatId));
        manager.setTittleForTask(data.replaceFirst("Тема", ""));
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        buttons.put("К списку подчиненных", "TASK_GIVE");
        String link = "";
        String text = "Введите дату исполения в формате:\n" +
                "Срок количество дней";
        return inlineKeyboardMaker.makeMessage(chatId, buttons, text, link);
    }

    public SendMessage setEnd(String chatId, String data) {
        User manager = userService.getUserByTelegramId(Long.parseLong(chatId));
        String[] split = data.split(" ");
        manager.setEndOfTask(LocalDateTime.now().plusDays(Long.parseLong(split[1])));
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        buttons.put("К списку подчиненных", "TASK_GIVE");
        String link = "";
        String text = "Введите текст задания в формате:\n" +
                "Задание текст задания";
        return inlineKeyboardMaker.makeMessage(chatId, buttons, text, link);
    }

    public SendMessage setText(String chatId, String data) {
        User manager = userService.getUserByTelegramId(Long.parseLong(chatId));
        manager.setTaskText(data.replaceFirst("Задание", ""));
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        buttons.put("1", "TASK_SETSCORE_1");
        buttons.put("2", "TASK_SETSCORE_2");
        buttons.put("3", "TASK_SETSCORE_3");
        buttons.put("4", "TASK_SETSCORE_4");
        buttons.put("5", "TASK_SETSCORE_5");
        buttons.put("К списку подчиненных", "TASK_GIVE");
        String link = "";
        String text = "Введите сложность задания";
        return inlineKeyboardMaker.makeMessage(chatId, buttons, text, link);
    }

    private SendMessage setScore(String chatId, String data) {
        User manager = userService.getUserByTelegramId(Long.parseLong(chatId));
        String[] split = data.split("_");
        manager.setTaskScore(Integer.parseInt(split[2]));
        createTask(manager);
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        buttons.put("К списку подчиненных", "TASK_GIVE");
        String link = "";
        String text = "Задание успешно создано и отправлено работнику";
        return inlineKeyboardMaker.makeMessage(chatId, buttons, text, link);
    }

    private void createTask(User manager) {
        Task task = new Task(
                null,
                manager,
                manager.getWorkerForTask(),
                manager.getTittleForTask(),
                manager.getTaskText(),
                manager.getTaskScore(),
                LocalDateTime.now(),
                manager.getEndOfTask(),
                Status.НОВАЯ);
        taskRepository.save(task);
        manager.setWorkerForTask(null);
        manager.setTittleForTask(null);
        manager.setTaskText(null);
        manager.setTaskScore(null);
        manager.setEndOfTask(null);
    }
}
