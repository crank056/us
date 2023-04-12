package crank.us.locations;

import crank.us.bot.InlineKeyboardMaker;
import crank.us.enums.TaskStatus;
import crank.us.exceptions.AccessException;
import crank.us.exceptions.ExistException;
import crank.us.models.Task;
import crank.us.models.User;
import crank.us.repositories.TaskRepository;
import crank.us.repositories.UserRepository;
import crank.us.services.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public SendMessage menuHandler(String chatId, String data) throws AccessException, ExistException {
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
            return getTaskGiveVar(chatId);
        } else if (data.equals("TASK_GIVE_WORKER")) {
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
        List<Task> taskList = taskRepository.getAllByWorkerIdAndTaskStatus(
                userService.getUserByTelegramId(Long.parseLong(chatId)).getId(),
                TaskStatus.valueOf(split[2]));
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
            if (task.getTaskStatus().equals(TaskStatus.НОВАЯ)) {
                buttons.put("Принять к исполнению", "TASK_SETSTATUS_" + TaskStatus.ВЫПОЛНЯЕТСЯ + "_" + task.getId());
            } else if (task.getTaskStatus().equals(TaskStatus.ВЫПОЛНЯЕТСЯ)) {
                buttons.put("Завершить", "TASK_SETSTATUS_" + TaskStatus.ЗАВЕРШЕНА + "_" + task.getId());
            }
        }
        if (task.getManager().getTelegramId().equals(Long.parseLong(chatId))) {
            if (task.getTaskStatus().equals(TaskStatus.НОВАЯ)
                    || task.getTaskStatus().equals(TaskStatus.ВЫПОЛНЯЕТСЯ)) {
                buttons.put("Отменить", "TASK_SETSTATUS_" + TaskStatus.ОТМЕНЕНА + "_" + task.getId());
            } else if (task.getTaskStatus().equals(TaskStatus.ЗАВЕРШЕНА)) {
                buttons.put("Согласовать", "TASK_SETSTATUS_" + TaskStatus.СОГЛАСОВАНА + "_" + task.getId());
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
        task.setTaskStatus(TaskStatus.valueOf(split[2]));
        if(split[2].equals(TaskStatus.СОГЛАСОВАНА.name())) {
            task.getWorker().setRating(task.getScore().longValue());
        }
        String text = "Статус установлен на " + task.getTaskStatus();
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
        buttons.put("Просроченные задания", "TASK_GETGIVELIST_ПРОСРОЧЕНА");
        String link = "";
        return inlineKeyboardMaker.makeMessage(chatId, buttons, "Статусы выданных заданий", link);
    }

    private SendMessage getGivedTasksList(String chatId, String data) {
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        String[] split = data.split("_");
        List<Task> taskList = new ArrayList<>();
        if (split[2].equals("ПРОСРОЧЕНА")) {
            taskList = taskRepository.getAllByManagerIdAndEndDateBefore(userService.getUserByTelegramId(Long.parseLong(chatId)).getId(),
                    LocalDateTime.now());
        } else {
            taskList = taskRepository.getAllByManagerIdAndTaskStatus(
                    userService.getUserByTelegramId(Long.parseLong(chatId)).getId(),
                    TaskStatus.valueOf(split[2]));
        }
        for (Task task : taskList) {
            if (split[2].equals("ПРОСРОЧЕНА") && (task.getTaskStatus().equals(TaskStatus.ОТМЕНЕНА)
                    || task.getTaskStatus().equals(TaskStatus.ЗАВЕРШЕНА)
                    || task.getTaskStatus().equals(TaskStatus.СОГЛАСОВАНА))) {
                {
                }
            } else {
                buttons.put("Для " + task.getWorker().getFirstName() + " " + task.getWorker().getLastName() + ": "
                        + task.getTittle(), "TASK_GET_" + task.getId());
            }
        }
        String link = "";
        return inlineKeyboardMaker.makeMessage(chatId, buttons, "Список задач со статусом " + split[2], link);
    }

    private SendMessage getTaskGiveVar(String chatId) {
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        String link = "";
        String text = "Выберите вариант:";
        buttons.put("Задание для всех подчиненных", "TASK_GIVE_ALL");
        buttons.put("Выбрать подчиненного", "TASK_GIVE_WORKER");
        return inlineKeyboardMaker.makeMessage(chatId, buttons, text, link);
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

    private SendMessage giveTask(String chatId, String data) throws AccessException, ExistException {
        if (!userService.getUserByTelegramId(Long.parseLong(chatId)).getIsManager()) {
            throw new AccessException("Вы не являетесь руководителем");
        }
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        String link = "";
        String[] split = data.split("_");
        User manager = userService.getUserByTelegramId(Long.parseLong(chatId));
        String text;
        if (!split[2].equals("ALL")) {
            if (userRepository.getReferenceById(Long.parseLong(split[2])).getTelegramId() == null) {
                throw new ExistException("Данный работник еще не регистрировался в системе");
            }
            manager.setWorkerForTask(userRepository.getReferenceById(Long.parseLong(split[2])));
            text = "Задание для " + manager.getWorkerForTask().getFirstName() + " " + manager.getWorkerForTask().getLastName() +
                    "\nСтатус работника: " + manager.getWorkerForTask().getStatus() + "\nВведите тему задания в формате:\n" +
                    "Тема текст темы";
        } else {
            manager.setWorkerForTask(null);
            text = "Задание для всех" + "\nВведите тему задания в формате:\n" +
                    "Тема текст темы";
        }
        buttons.put("В меню", "TASK_GIVE");
        return inlineKeyboardMaker.makeMessage(chatId, buttons, text, link);
    }

    public SendMessage setTittle(String chatId, String data) {
        User manager = userService.getUserByTelegramId(Long.parseLong(chatId));
        manager.setTittleForTask(data.replaceFirst("Тема", ""));
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        buttons.put("В меню", "TASK_GIVE");
        String link = "";
        String text;
        if (manager.getWorkerForTask() != null) {
            text = "Задание для " + manager.getWorkerForTask().getFirstName() + " " + manager.getWorkerForTask().getLastName() +
                    "\nСтатус работника: " + manager.getWorkerForTask().getStatus() + "\nВведите срок исполения в формате:\n" +
                    "Срок количество дней";
        } else {
            text = "Задание для всех" + "\nВведите срок исполнения в формате:\n" +
                    "Срок количество дней";
        }
        return inlineKeyboardMaker.makeMessage(chatId, buttons, text, link);
    }

    public SendMessage setEnd(String chatId, String data) {
        User manager = userService.getUserByTelegramId(Long.parseLong(chatId));
        String[] split = data.split(" ");
        manager.setEndOfTask(LocalDateTime.now().plusDays(Long.parseLong(split[1])));
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        buttons.put("В меню", "TASK_GIVE");
        String link = "";
        String text;
        if (manager.getWorkerForTask() != null) {
            text = "Задание для " + manager.getWorkerForTask().getFirstName() + " " + manager.getWorkerForTask().getLastName() +
                    "\nСтатус работника: " + manager.getWorkerForTask().getStatus() + "\nВведите текст задания в формате:\n" +
                    "Задание текст задания";
        } else {
            text = "Задание для всех" + "\nВведите текст задания в формате:\n" +
                    "Задание текст задания";
        }
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
        buttons.put("В меню", "TASK_GIVE");
        String link = "";
        String text;
        if (manager.getWorkerForTask() != null) {
            text = "Задание для " + manager.getWorkerForTask().getFirstName() + " " + manager.getWorkerForTask().getLastName() +
                    "\nСтатус работника: " + manager.getWorkerForTask().getStatus() + "\nВведите сложность задания";
        } else {
            text = "Задание для всех" + "\nВведите сложность задания";
        }
        return inlineKeyboardMaker.makeMessage(chatId, buttons, text, link);
    }

    private SendMessage setScore(String chatId, String data) {
        User manager = userService.getUserByTelegramId(Long.parseLong(chatId));
        String[] split = data.split("_");
        manager.setTaskScore(Integer.parseInt(split[2]));
        String text;
        if (manager.getWorkerForTask() != null) {
            text = "Задание для " + manager.getWorkerForTask().getFirstName() + " " + manager.getWorkerForTask().getLastName() +
                    "\nСтатус работника: " + manager.getWorkerForTask().getStatus() + "\nЗадание успешно создано и отправлено работнику";
            createTask(manager);
        } else {
            text = "Задание успешно создано и отправлено работникам";
            createMassTask(manager);
        }
        LinkedHashMap<String, String> buttons = new LinkedHashMap<>();
        buttons.put("В меню", "TASK_GIVE");
        String link = "";
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
                TaskStatus.НОВАЯ);
        taskRepository.save(task);
        manager.setWorkerForTask(null);
        manager.setTittleForTask(null);
        manager.setTaskText(null);
        manager.setTaskScore(null);
        manager.setEndOfTask(null);
    }

    private void createMassTask(User manager) {
        List<User> workers = userRepository.getAllByManagerId(manager.getId());
        for (User worker : workers) {
            if (worker.getTelegramId() != null) {
                Task task = new Task(
                        null,
                        manager,
                        worker,
                        manager.getTittleForTask(),
                        manager.getTaskText(),
                        manager.getTaskScore(),
                        LocalDateTime.now(),
                        manager.getEndOfTask(),
                        TaskStatus.НОВАЯ);
                taskRepository.save(task);
            }
        }
        manager.setWorkerForTask(null);
        manager.setTittleForTask(null);
        manager.setTaskText(null);
        manager.setTaskScore(null);
        manager.setEndOfTask(null);
    }
}
