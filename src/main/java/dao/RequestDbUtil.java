package dao;

import model.Currency;
import utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class RequestDbUtil {
    /**
     * Метод для получения валюты по заданному коду.
     * Example: GET /currency/EUR
     */
    public static Currency findCurrencyByCode(String code) {
        String sql = """
                    SELECT *
                    FROM Currencies 
                    WHERE code = ?""";

        Currency curr = new Currency();

        try (
                Connection con = ConnectionManager.open();
                PreparedStatement stmt = con.prepareStatement(sql);
        )
        {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();

            curr = new Currency(rs.getInt("id"),
                    rs.getString("code"),
                    rs.getString("full_name"),
                    rs.getString("sign"));
        }
        catch(SQLException e)
        {
            e.printStackTrace(System.err);
        }
        return curr;
    }

    /**
    * Метод для получения всех валют из таблицы Currencies,
     * для GET /currencies
    * */
    public static List<Currency> selectAllCurrencies() {
        String sql = """
                SELECT *
                FROM Currencies""";
        List<Currency> currencies = new ArrayList<>();
        try(
                Connection connection = ConnectionManager.open();
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                currencies.add(new Currency(rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("full_name"),
                        rs.getString("sign")));
            }

        } catch(SQLException e) {
            e.printStackTrace(System.err);
        }
        return currencies;
    }

    /**
     * Метод для добавления в таблицу новой валюты,
     * для POST /currencies (code, name and sign передаются в теле запроса)
     * */
    public static void insertCurrency(Currency curr) {
        if (findCurrencyByCode(curr.getCode()).getId() != 0) {
            System.out.println("Такая валюта уже существует");
        }
        else {
            String sql = """
                    INSERT INTO Currencies(code, full_name, sign)
                    VALUES (?, ?, ?)""";

            try (
                    Connection connection = ConnectionManager.open();
                    PreparedStatement statement = connection.prepareStatement(sql);
            ) {

                statement.setString(1, curr.getCode());
                statement.setString(2, curr.getName());
                statement.setString(3, curr.getSign());
                statement.execute();
                System.out.println("Валюта успешно добавлена");

            } catch (SQLException ex) {
                throw new RuntimeException("Валюта не добавлена");
            }
        }
    }
}
