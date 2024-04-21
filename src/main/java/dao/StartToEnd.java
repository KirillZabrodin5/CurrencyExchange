package dao;

/**
 * Класс для определения, существует ли прямой курс перевода,
 * обратный курс или такого курса нет вообще
 * и вызывает соответствующую реализацию
 */
public final class StartToEnd {
    //start valut
    //end valut
    private String startCodeCurrency;
    private String endCodeCurrency;

    //StartToEnd(start, end)
    public StartToEnd(String startCodeCurrency, String endCodeCurrency) {
        this.startCodeCurrency = startCodeCurrency;
        this.endCodeCurrency = endCodeCurrency;
    }

    //building a road()
    private void buildRoad() {
        //Логика следующая: если есть прямой перевод, то работаем с классом
        //DirectTranslation(А, Б)

        //Если прямого перевода нет, но есть БА, то вызываем DirectTranslation(Б, А)
        //и как-то обрабатываем полученный ответ, чтобы получить АБ

        //Если нужен перевод из А в Б, и нет не АБ, не БА, то надо искать промежуточную
        //вылюту. В моем случае это доллар. Пример: EUR -> RUB, переводим из
        //EUR to USD, USD -> RUB ну и получаем ответ. Переводы осуществляем через
        //DirectTranslation
    }

}
