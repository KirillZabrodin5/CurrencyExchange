package dao;

import utils.ConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Перевод из А в Б
 * Если получили USD, RUB от юзера, то
 * метод translate вернет 63.75, так как 1 доллар = 63.75 рублей
 */

public class Translation {
    /**
    * Делаем запрос к таблице ExchangeRates и возвращаем rate
     */
    public static double translate(int idStartCurrency, int idEndCurrency) {
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
