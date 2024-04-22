package dao;

import model.Currency;
import utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Класс для определения, существует ли прямой курс перевода,
 * обратный курс или такого курса нет вообще
 * и вызывает соответствующую реализацию
 */
public final class ReceivedRate {
    private int idStartCurrency;
    private int idEndCurrency;

    public ReceivedRate(String startCodeCurrency, String endCodeCurrency) {
        idStartCurrency = RequestDbUtil.findCurrencyByCode(startCodeCurrency).getId();
        idEndCurrency = RequestDbUtil.findCurrencyByCode(endCodeCurrency).getId();
    }

    public double translateBuild() {
        return buildRoad();
    }
    private double buildRoad() {
        double answerAB = -1;

        if (idStartCurrency == 0 || idEndCurrency == 0 || idStartCurrency == idEndCurrency) {
            System.out.println("Вы явно что-то делаете не так");
            return answerAB;
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
                answerAB = directTranslate();
            } else {
                //если прямого перевода нет, то 2 случая:
                //есть перевод BA и есть перевод с промежуточной валютой USD
                statement.setInt(1, idEndCurrency);
                statement.setInt(2, idStartCurrency);
                rs = statement.executeQuery();
                if (rs.getInt(1) == 1) {
                    //BA
                    answerAB = indirectTranslate();
                } else {
                    //перевод с промежуточной валютой USD
                    answerAB = translationWithIntermediateMeaning();
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return answerAB;
    }

    private double directTranslate() {
        return Translation.translate(idStartCurrency, idEndCurrency);
    }

    private double indirectTranslate() {
        return 1 / Translation.translate(idEndCurrency,
                idStartCurrency);
    }

    private double translationWithIntermediateMeaning() {
        Currency currency = RequestDbUtil.findCurrencyByCode("USD");
        int idUSD = currency.getId();

        double USDtoStart = Translation
                .translate(idUSD, idStartCurrency);

        double USDtoEnd = Translation
                .translate(idUSD, idEndCurrency);
        return USDtoEnd / USDtoStart;
    }
}
