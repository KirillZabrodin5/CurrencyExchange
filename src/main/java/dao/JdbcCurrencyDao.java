package dao;

import Exceptions.DatabaseUnavailableException;
import Exceptions.EntityExistsException;
import Exceptions.NotFoundException;
import dto.CurrencyDto;
import entities.Currency;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;
import utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCurrencyDao implements CurrencyDao {
    /**
     * Метод для получения всех валют из таблицы Currencies,
     * для GET /currencies
     * Возможны 2 статуса ответов - 200 (все хорошо) или 500 (бд недоступна или что-то еще)
     */
    @Override
    public List<Currency> findAll() {
        final String sqlQuery = """
                SELECT *
                FROM Currencies""";
        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement stmt = connection.prepareStatement(sqlQuery)
        ) {
            List<Currency> currencies = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                currencies.add(getCurrencyFromResultSet(rs));
            }
            return currencies;
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Database unavailable");
        }
    }

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
    public Optional<Currency> findByCode(CurrencyDto currencyDto) {
        final String sqlQuery = """
                SELECT *
                FROM Currencies 
                WHERE code = ?""";
        try (
                Connection con = ConnectionManager.open();
                PreparedStatement stmt = con.prepareStatement(sqlQuery);
        ) {
            stmt.setString(1, currencyDto.getCode());
            ResultSet rs = stmt.executeQuery();
            Currency currency = getCurrencyFromResultSet(rs);
            if (!rs.next()) {
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
     * Отсутствует нужное поле формы - 400 (эту ошибку где-то выше по слоям надо обрабатывать)
     * Валюта с таким кодом уже существует - 409
     * Ошибка (например, база данных недоступна) - 500
     */
    @Override
    public Optional<Currency> save(CurrencyDto currencyDto) {
        final String sqlQuery = """
                INSERT INTO Currencies(code, full_name, sign)
                VALUES (?, ?, ?)
                RETURNING *""";

        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sqlQuery)
        ) {
            statement.setString(1, currencyDto.getCode());
            statement.setString(2, currencyDto.getName());
            statement.setString(3, currencyDto.getSign());
            ResultSet rs = statement.executeQuery();
            Currency currency = getCurrencyFromResultSet(rs);
            return Optional.of(currency);
        } catch (SQLException ex) {
            if (ex instanceof SQLiteException sqLiteException) {
                if (sqLiteException.getResultCode().code ==
                        SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code) {
                    throw new EntityExistsException("Currency already exists");
                }
            }
            throw new DatabaseUnavailableException("Database unavailable");
        }
    }

    /**
     * HTTP коды ответов:
     * Успех - 201
     * Отсутствует нужное поле формы - 400 (эту ошибку где-то выше по слоям надо обрабатывать)
     * Валюта с таким кодом отсутствует в таблице - 409
     * Ошибка (например, база данных недоступна) - 500
     */
    @Override
    public Optional<Currency> delete(CurrencyDto curr) {
        final String sqlQuery = """
                DELETE FROM Currencies
                WHERE code = ?
                RETURNING *
                UPDATE sqlite_sequence 
                SET seq = (SELECT MAX(id) FROM Currencies)
                WHERE name = 'Currencies'""";
        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sqlQuery)
        ) {
            statement.setString(1, curr.getCode());
            ResultSet rs = statement.executeQuery();
            Currency currency = getCurrencyFromResultSet(rs);
            if (!rs.next()) {
                throw new NotFoundException("Currency with code " + curr.getCode() + " not found");
            }
            return Optional.of(currency);
        } catch (SQLException ex) {
            throw new DatabaseUnavailableException("Database unavailable");
        }
    }

    /**
     * Метод для ExchangeRate
     * HTTP коды ответов:
     * Успех - 200
     * Код валюты отсутствует в адресе - 400 (эту ошибку где-то выше по слоям надо обрабатывать)
     * Валюта не найдена - 404
     * Ошибка (например, база данных недоступна) - 500
     */
    @Override
    public Optional<Currency> findById(Currency currencyInput) {
        final String sqlQuery = """
                SELECT *
                FROM Currencies 
                WHERE id = ?""";
        try (
                Connection con = ConnectionManager.open();
                PreparedStatement stmt = con.prepareStatement(sqlQuery);
        ) {
            stmt.setLong(1, currencyInput.getId());
            ResultSet rs = stmt.executeQuery();
            Currency currency = getCurrencyFromResultSet(rs);
            if (!rs.next()) {
                throw new NotFoundException("Currency with id " + currencyInput.getId()
                        + " not found");
            }
            return Optional.of(currency);
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Database unavailable");
        }
    }

    private Currency getCurrencyFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String code = rs.getString("code");
        String fullName = rs.getString("full_name");
        String sign = rs.getString("sign");
        return new Currency(id, code, fullName, sign);
    }
}
