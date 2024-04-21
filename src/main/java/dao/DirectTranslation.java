package dao;

import utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * прямой перевод из А в Б
 */

public class DirectTranslation {
    private String startCodeCurrency;
    private String endCodeCurrency;

    public DirectTranslation(String startCodeCurrency, String endCodeCurrency) {
        this.startCodeCurrency = startCodeCurrency;
        this.endCodeCurrency = endCodeCurrency;;
    }

    public double translate() {
        //делаем запрос к ExchangeRates и возвращаем rate

        int idStartCurrency = RequestToDbUtil.answerFromDB(startCodeCurrency).getId();
        int idEndCurrency = RequestToDbUtil.answerFromDB(endCodeCurrency).getId();

        String sql = """
                SELECT rate
                FROM ExchangeRates
                WHERE base_currency_id = ?
                and target_currency_id = ?""";
        double rate;

        try(
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sql);
        )
        {
            statement.setDouble(1, idStartCurrency);
            statement.setDouble(2, idEndCurrency);
            ResultSet rs = statement.executeQuery();
            rate = rs.getDouble("rate");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return rate;
    }


}
