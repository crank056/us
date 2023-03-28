package crank.us.enums;

public enum BotMessages {
    WELCOME("\uD83D\uDC4B " + "Добро пожаловать в Пригорье! \n\n" +
            "Издавна его живели смотрели на пик самой высокой горы и мечтали его занять. " +
            "Зачем им это нужно никто не знает, но говорят что он обладает волшебной силой," +
            " ну или просто сеть лучше ловит. \n\n" +
            "А теперь у каждого желающего есть возможность стать царем горы. \n\n" +
            "Дай поддых своему ближнему и займи его место под солнцем, " +
            "достигни вершин рейтинга и стань царем горы! \nУдачи!\n\n" +
            "❗❗❗Введи в поле сообщений свой ник❗❗❗ \n"),

    REG("В Предгорье для тебя есть много всего интересного. \n\n" +
            " Если ты тут новичок первым делом советуем отдохнуть," +
            " ведь ты проделал огромный путь прежде чем оказаться тут. Зайди в меню персонажа и выбери отдых. \n\n" +
            "Отдых поможет тебе восстановить здоровье и энергию. Энергия необходима для любый действий, а большое здоровье поможет не упасть в бою." +
            " Но отдыхать бесконечно не получится. Заходи сюда раз в час и расслабляйся. \n" +
            "Восстановлению энергии и здоровья также помогут зелья купленные в магазине. \n\n" +
            "В магазине можно приобрести оружие, броню и зелья. Аммуниция дает большое преимущество бою." +
            " После покупки ты можешь сразу надеть броню в магазине, а можешь и нести домой в пакете." +
            " Больше одной единицы купить не получится, а продать назад выйдет только за 80 % от стоимости." +
            "\n\nДля легкого заработка можно сходить на завод по производству горликов," +
            " а для сложного и опасного добро пожаловать на арену. Тут ты сможешь дать в пятак первому встречному," +
            " победить его и забрать горлики, честь и славу, но можешь и не победить." +
            " Тогда противник будет не против получить что-то взамен, чтобы отпустить тебя живым." +
            " Атаковать персонажа можно не чаще чем один раз в час, используй это время правильно. " +
            "Если в данный момент у тебя нет подходящего противника  - атакую фантома." +
            " У него всегда немного горликов, а по силе он равен тебе." +
            "\n\nВ рейтинге можно посмотреть на главных рабочих и забияк пригорья," +
            " ну и прикинуть сколько носиков еще нужно сломать чтобы стать царем горы.");

    private final String message;

    BotMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}