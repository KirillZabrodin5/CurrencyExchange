package dao;

import model.Currency;
import utils.ConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//тут не хватает из интерфейса реализованных методов update and delete

public class JdbcCurrencyDao implements CrudDao<Currency, String> {

    /**
     * Метод для получения всех валют из таблицы Currencies,
     * для GET /currencies
     * Возможны 2 статуса ответов - 200 (все хорошо) или 500 (бд недоступна или что-то еще)
     */
    @Override
    public List<Currency> findAll() {
        final String sql = """
                SELECT *
                FROM Currencies""";
        List<Currency> currencies = new ArrayList<>();
        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                currencies.add(getCurrencyFromResultSet(rs));
            }

        } catch (SQLException e) {
            //TODO 500 (БАЗА ДАННЫХ НЕДОСТУПНА) STATUS
        }
        return currencies;
    }

    /**
     * Метод для получения валюты по заданному коду.
     * Example: GET /currency/EUR
     *
     * HTTP коды ответов:
     * Успех - 200
     * Код валюты отсутствует в адресе - 400
     * Валюта не найдена - 404
     * Ошибка (например, база данных недоступна) - 500
     */
    @Override
    public Optional<Currency> findByCode(String code) {
        final String sql = """
                SELECT *
                FROM Currencies 
                WHERE code = ?""";

        try (
                Connection con = ConnectionManager.open();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();

            return Optional.of(getCurrencyFromResultSet(rs));
        } catch (SQLException e) {
            //TODO
        }
        return Optional.empty();
    }



    /**
     * Метод для добавления в таблицу новой валюты,
     * для POST /currencies (code, name and sign передаются в теле запроса)
     *
     * HTTP коды ответов:
     * Успех - 201
     * Отсутствует нужное поле формы - 400
     * Валюта с таким кодом уже существует - 409
     * Ошибка (например, база данных недоступна) - 500
     */
    @Override
    public void save(Currency curr) {

        final String sql = """
                INSERT INTO Currencies(code, full_name, sign)
                VALUES (?, ?, ?)""";

        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, curr.getCode());
            statement.setString(2, curr.getName());
            statement.setString(3, curr.getSign());
            statement.execute();

        } catch (SQLException ex) {
            //TODO
        }
    }

    @Override
    public Optional<Currency> findById(int id) {
        final String sql = """
                SELECT *
                FROM Currencies 
                WHERE id = ?""";
        try (
                Connection con = ConnectionManager.open();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            return Optional.of(getCurrencyFromResultSet(rs));
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return Optional.empty();
    }

    private Currency getCurrencyFromResultSet(ResultSet rs) throws SQLException {
        return new Currency(rs.getInt("id"),
                rs.getString("code"),
                rs.getString("full_name"),
                rs.getString("sign"));
    }
}
