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
 * Основой метод класса - translate, с помощью него получаем
 * определяем маршрут перевода и возвращаем rate
 */
public final class ReceivedRate {
    private int idStartCurrency;
    private int idEndCurrency;

    public ReceivedRate(String startCodeCurrency, String endCodeCurrency) {
        idStartCurrency = RequestDb.getCurrencyByCode(startCodeCurrency).getId();
        idEndCurrency = RequestDb.getCurrencyByCode(endCodeCurrency).getId();
    }

    public double translate() {
        double answer = -1;

        if (idStartCurrency == 0 || idEndCurrency == 0) {
            System.out.println("Некорректный ввод, одной из (двух) валют не существует. " +
                    "Посмотрите список существующих валют и " +
                    "повторите еще раз.");
            return answer;
        }
        if (idStartCurrency == idEndCurrency) {
            return 1;
            //здесь надо как-то так описать, чтобы rate = 1
        }

        String sql = """
                SELECT count(*)
                FROM ExchangeRates
                WHERE base_currency_id = ?
                and target_currency_id = ?
                """;

        try(
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sql);
        )
        {
            statement.setInt(1, idStartCurrency);
            statement.setInt(2, idEndCurrency);
            ResultSet rs = statement.executeQuery();

            int result = rs.getInt(1);
            if (result == 1) {
                //если есть прямой перевод, то работаем
                answer = directTranslate();
            } else {
                //если прямого перевода нет, то 2 случая:
                //есть перевод BA и есть перевод с промежуточной валютой USD
                statement.setInt(1, idEndCurrency);
                statement.setInt(2, idStartCurrency);
                rs = statement.executeQuery();
                if (rs.getInt(1) == 1) {
                    //BA
                    answer = indirectTranslate();
                } else {
                    //перевод с промежуточной валютой USD
                    answer = translationWithIntermediateMeaning();
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return answer;
    }

    private double directTranslate() {
        return Translation.getRate(idStartCurrency, idEndCurrency);
    }

    private double indirectTranslate() {
        return 1 / Translation.getRate(idEndCurrency, idStartCurrency);
    }

    private double translationWithIntermediateMeaning() {
        Currency currency = RequestDb.getCurrencyByCode("USD");
        int idUSD = currency.getId();

        double USDtoStart = Translation
                .getRate(idUSD, idStartCurrency);

        double USDtoEnd = Translation
                .getRate(idUSD, idEndCurrency);
        return USDtoEnd / USDtoStart;
    }
}
