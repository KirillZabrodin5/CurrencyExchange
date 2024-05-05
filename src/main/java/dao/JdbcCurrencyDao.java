package dao;

import Exceptions.DatabaseUnavailableException;
import Exceptions.NotFoundException;
import model.Currency;
import utils.ConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//этот класс норм написан, можно так оставить

public class JdbcCurrencyDao implements CurrencyDao {
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
            throw new DatabaseUnavailableException("Database unavailable");
        }
        return currencies;
    }

    //todo 400 error
    /**
     * Метод для получения валюты по заданному коду.
     * Example: GET /currency/EUR
     * HTTP коды ответов:
     * Успех - 200
     * Код валюты отсутствует в адресе - 400 (эту ошибку где-то выше по слоям надо обрабатывать)
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
            Currency currency = getCurrencyFromResultSet(rs);
            if(currency == null) {
                throw new NotFoundException("Currency not found");
            }
            return Optional.of(currency);
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Database unavailable");
        }
    }

    /**
     * Метод для добавления в таблицу новой валюты,
     * для POST /currencies (code, name and sign передаются в теле запроса)
     * HTTP коды ответов:
     * Успех - 201
     * Отсутствует нужное поле формы - 400
     * Валюта с таким кодом уже существует - 409
     * Ошибка (например, база данных недоступна) - 500
     */
    @Override
    public Optional<Currency> save(Currency curr) {

        final String sql = """
                INSERT INTO Currencies(code, full_name, sign)
                VALUES (?, ?, ?)
                RETURNING *""";

        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, curr.getCode());
            statement.setString(2, curr.getName());
            statement.setString(3, curr.getSign());
            ResultSet rs = statement.executeQuery();

            return Optional.of(getCurrencyFromResultSet(rs));
        } catch (SQLException ex) {
            //TODO
        }
        return Optional.empty();
    }

    @Override
    public Optional<Currency> delete(Currency curr) {

        final String sql = """
                DELETE FROM Currencies
                WHERE code = ?
                RETURNING *""";

        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, curr.getCode());
            ResultSet rs = statement.executeQuery();

            return Optional.of(getCurrencyFromResultSet(rs));
        } catch (SQLException ex) {
            //TODO
        }
        return Optional.empty();
    }

    /**
     * Метод для ExchangeRate
     * */
    @Override
    public Optional<Currency> findById(Long id) {
        final String sql = """
                SELECT *
                FROM Currencies 
                WHERE id = ?""";
        try (
                Connection con = ConnectionManager.open();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            return Optional.of(getCurrencyFromResultSet(rs));
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return Optional.empty();
    }

    private Currency getCurrencyFromResultSet(ResultSet rs)  {
        try {

            Long id = rs.getLong("id");
            String code = rs.getString("code");
            String fullName = rs.getString("full_name");
            String sign = rs.getString("sign");

            if (code == null) {
                return null;
            }
            return new Currency(id, code, fullName, sign);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
