package dao;

import model.Currency;
import utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Класс для определения, существует ли прямой маршрут перевода,
 * обратный курс или такого курса нет вообще
 * и вызывает соответствующую реализацию.
 * Основой метод класса - translate, с помощью него
 * определяем маршрут перевода и возвращаем rate
 */
public final class ReceivedRate {
    private final Long idStartCurrency;
    private final Long idEndCurrency;
    private final JdbcExchangeRateDao jdbcExchangeRateDao = new JdbcExchangeRateDao();

    public ReceivedRate(String startCodeCurrency, String endCodeCurrency) {
        JdbcCurrencyDao dao = new JdbcCurrencyDao();
        idStartCurrency = dao
                .findByCode(startCodeCurrency)
                .orElse(null)
                .getId();
        idEndCurrency = dao
                .findByCode(endCodeCurrency)
                .orElse(null)
                .getId();

    }

    public double translate() {
        double answer = -1;

        if (idStartCurrency == 0 || idEndCurrency == 0) {
            System.out.println("Некорректный ввод, одной из (двух) валют не существует. " +
                    "Посмотрите список существующих валют и " +
                    "повторите еще раз.");
            return answer;
        }
        if (idStartCurrency.equals(idEndCurrency)) {
            return 1;
            //здесь надо как-то так описать, чтобы rate = 1
        }

        String sql = """
                SELECT count(*)
                FROM ExchangeRates
                WHERE base_currency_id = ?
                and target_currency_id = ?
                """;

        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setLong(1, idStartCurrency);
            statement.setLong(2, idEndCurrency);
            ResultSet rs = statement.executeQuery();

            int result = rs.getInt(1);
            if (result == 1) {
                //если есть прямой перевод, то работаем
                answer = directTranslate();
            } else {
                //если прямого перевода нет, то 2 случая:
                //есть перевод BA и есть перевод с промежуточной валютой USD
                statement.setLong(1, idEndCurrency);
                statement.setLong(2, idStartCurrency);
                rs = statement.executeQuery();
                if (rs.getInt(1) == 1) {
                    //BA
                    answer = indirectTranslate();
                } else {
                    //перевод с промежуточной валютой USD
                    answer = translationWithIntermediateMeaning();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return answer;
    }

    private double directTranslate() {
        return jdbcExchangeRateDao.getRate(idStartCurrency, idEndCurrency);
    }

    private double indirectTranslate() {
        return 1 / jdbcExchangeRateDao.getRate(idEndCurrency, idStartCurrency);
    }

    private double translationWithIntermediateMeaning() {
        JdbcCurrencyDao dao = new JdbcCurrencyDao();
        Currency currency = dao.findByCode("USD").get();
        Long idUSD = currency.getId();

        double USDtoStart = jdbcExchangeRateDao
                .getRate(idUSD, idStartCurrency);

        double USDtoEnd = jdbcExchangeRateDao
                .getRate(idUSD, idEndCurrency);
        return USDtoEnd / USDtoStart;
    }
}
