package dao;

import Exceptions.DatabaseUnavailableException;
import Exceptions.EntityExistsException;
import Exceptions.NotFoundException;
import entities.Currency;
import entities.ExchangeRate;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;
import utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class JdbcExchangeRateDao implements ExchangeRateDao {
    /**
     * Метод, который должен в каком-то виде, возвращать все обменные курсы.
     * Как это организовать я пока не понимаю
     * Этот метод предназначен для запроса: GET /exchangeRates
     * HTTP коды ответов:
     * Успех - 200
     * Ошибка (например, база данных недоступна) - 500
     */
    @Override
    public List<ExchangeRate> findAll() {
        final String sql = """
                SELECT id,
                       base_currency_id,
                       target_currency_id,
                       rate
                FROM ExchangeRates""";
        List<ExchangeRate> list = new ArrayList<>();
        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                list.add(getExchangeRate(resultSet));
            }
        } catch (SQLException ex) {
            throw new DatabaseUnavailableException("Database unavailable");
        }
        return list;
    }

    /**
     * Метод для получения по кодам 2 валют их обменный курс: id курса,
     * код стартовой валюты, код конечной валюты, ставка.
     * Использоваться будет для запроса: GET /exchangeRate/USDRUB
     * HTTP коды ответов:
     * Успех - 200
     * Коды валют пары отсутствуют в адресе - 400 (это где-то выше в слоях надо обрабатывать)
     * Обменный курс для пары не найден - 404
     * Ошибка (например, база данных недоступна) - 500
     */
    @Override
    public Optional<ExchangeRate> findByCode(String baseCode, String targetCode) {
        final String sqlQuery = """
                SELECT ex.id,
                       (SELECT id
                        FROM Currencies as c
                        WHERE c.code = ?) as base,
                       (SELECT id
                        FROM Currencies as c
                        WHERE c.code = ?) as target,
                       ex.rate
                FROM ExchangeRates as ex
                WHERE ex.base_currency_id = base AND
                      ex.target_currency_id = target;""";

        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sqlQuery);
        ) {
            statement.setString(1, baseCode);
            statement.setString(2, targetCode);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                throw new NotFoundException("Exchange Rate not found");
            }

            ExchangeRate exchangeRate = getExchangeRate(resultSet);
            return Optional.of(exchangeRate);
        } catch (SQLException ex) {
            throw new DatabaseUnavailableException("Database unavailable");
        }
    }

    /**
     * Метод, который получает на вход коды валют и ставку, а потом добавляет
     * эти данные в таблицу ExchangeRate.
     * Метод написан под запрос: POST /exchangeRates
     * HTTP коды ответов:
     * Успех - 201
     * Отсутствует нужное поле формы - 400 (это выше где-то надо обрабатывать)
     * Валютная пара с таким кодом уже существует - 409
     * Одна (или обе) валюта из валютной пары не существует в БД - 404
     * Ошибка (например, база данных недоступна) - 500
     */
    //в слое выше стоит из кодов валют и ставки создавать объект класса ExchangeRate
    @Override
    public Optional<ExchangeRate> save(ExchangeRate exchangeRateInput) {
        final String sqlQuery = """
                INSERT INTO ExchangeRates(base_currency_id, 
                target_currency_id, rate)
                VALUES (?, ?, ?)
                RETURNING *""";

        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sqlQuery);
        ) {
            Long idStart = exchangeRateInput.getBaseCurrency().getId();
            Long idEnd = exchangeRateInput.getTargetCurrency().getId();

            if ((Objects.equals(idStart, idEnd)) || (idStart == 0 || idEnd == 0)) {
                throw new NotFoundException("Exchange rate not found");
            }

            statement.setLong(1, idStart);
            statement.setLong(2, idEnd);
            statement.setDouble(3, exchangeRateInput.getRate());
            ResultSet rs = statement.executeQuery();
            return Optional.of(getExchangeRate(rs));
        } catch (SQLException ex) {
            if (ex instanceof SQLiteException sqLiteException) {
                if (sqLiteException.getResultCode().code ==
                        SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code) {
                    throw new EntityExistsException("Exchange rate already exists");
                }
            }
            throw new DatabaseUnavailableException("Database unavailable");
        }
    }

    @Override
    public Optional<ExchangeRate> delete(ExchangeRate exchangeRateInput) {
        final String sqlQuery = """
                DELETE FROM ExchangeRate
                WHERE base_currency_id = ? AND
                target_currency_id = ?
                RETURNING *""";
        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sqlQuery)
        ) {
            Long idStart = exchangeRateInput.getBaseCurrency().getId();
            Long idEnd = exchangeRateInput.getTargetCurrency().getId();
            if (idStart == 0L || idEnd == 0L) {
                throw new NotFoundException("Currency not found");
            }
            statement.setLong(1, idStart);
            statement.setLong(2, idEnd);
            ResultSet rs = statement.executeQuery();
            return Optional.of(getExchangeRate(rs));
        } catch (SQLException ex) {
            throw new DatabaseUnavailableException("Database unavailable");
        }
    }

    /**
     * Метод, который получает на вход коды валют и ставку, а потом обновляет
     * эти данные в таблице ExchangeRate.
     * Метод написан под запрос: PATCH /exchangeRate/USDRUB
     * HTTP коды ответов:
     * Успех - 200
     * Отсутствует нужное поле формы - 400 (это в слое выше обработать)
     * Валютная пара отсутствует в базе данных - 404
     * Ошибка (например, база данных недоступна) - 500
     */
    @Override
    public Optional<ExchangeRate> update(ExchangeRate exchangeRate) {
        final String sqlQuery = """
                UPDATE ExchangeRates
                SET rate = ?
                WHERE base_currency_id = ? AND
                      target_currency_id = ?
                RETURNING id, base_currency_id, 
                target_currency_id, rate""";
        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sqlQuery);
        ) {
            Long idStart = exchangeRate.getBaseCurrency().getId();
            Long idEnd = exchangeRate.getTargetCurrency().getId();

            if (idStart == 0L || idEnd == 0L) {
                throw new NotFoundException("Currency not found");
            }
            statement.setDouble(1, exchangeRate.getRate());
            statement.setLong(2, idStart);
            statement.setLong(3, idEnd);
            ResultSet rs = statement.executeQuery();
            return Optional.of(getExchangeRate(rs));
        } catch (SQLException ex) {
            throw new DatabaseUnavailableException("Database unavailable");
        }
    }

    /**
     * Делаем запрос к таблице ExchangeRate и возвращаем rate
     */
    public double getRate(Long idStartCurrency, Long idEndCurrency) {
        final String sqlQuery = """
                SELECT rate
                FROM ExchangeRates
                WHERE base_currency_id = ?
                and target_currency_id = ?""";
        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sqlQuery);
        ) {
            JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();
            jdbcCurrencyDao.findById(idStartCurrency).orElseThrow();
            jdbcCurrencyDao.findById(idEndCurrency).orElseThrow();
            statement.setDouble(1, idStartCurrency);
            statement.setDouble(2, idEndCurrency);

            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                throw new NotFoundException("Exchange Rate not found");
            }

            return rs.getDouble("rate");
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Database unavailable");
        }
    }

    private ExchangeRate getExchangeRate(ResultSet resultSet) throws SQLException {
        JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();
        ExchangeRate exchangeRate;
        long id = resultSet.getLong(1);
        if (id == 0L) {
            throw new NotFoundException("Exchange Rate not found");
        }
        Long id1 = resultSet.getLong(2);
        Currency currency1 = jdbcCurrencyDao.
                findById(id1)
                .orElse(null);

        Long id2 = resultSet.getLong(3);
        Currency currency2 = jdbcCurrencyDao.
                findById(id2)
                .orElse(null);

        double rate = resultSet.getDouble(4);
        exchangeRate = new ExchangeRate(
                id,
                currency1,
                currency2,
                rate
        );

        return exchangeRate;
    }

    @Override
    public Optional<ExchangeRate> findById(Long id) {
        final String sqlQuery = """
                SELECT * FROM ExchangeRates
                WHERE id = ?
                """;
        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sqlQuery);
        ) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                throw new NotFoundException("Exchange Rate not found");
            }
            return Optional.of(getExchangeRate(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //TODO этот метод не нужен вроде. Подумать над ним
//    /**
//     * Метод для получения id пары обменного курса из одной валюты в другую
//     */
//    private Long getId(String baseCode, String targetCode) {
//        long idExRate;
//        final String sqlQuery = """
//                SELECT id
//                FROM ExchangeRates
//                WHERE base_currency_id = ? and
//                target_currency_id = ?""";
//        try (
//                Connection connection = ConnectionManager.open();
//                PreparedStatement stmt = connection.prepareStatement(sqlQuery)
//        ) {
//            JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();
//            Currency baseCurrencyDto = new Currency(baseCode);
//            Currency targetCurrencyDto = new Currency(targetCode);
//            stmt.setLong(1,
//                    jdbcCurrencyDao.findByCode(baseCurrencyDto).orElseThrow().getId());
//            stmt.setLong(2,
//                    jdbcCurrencyDao.findByCode(targetCurrencyDto).orElseThrow().getId());
//            idExRate = stmt.executeQuery().getLong(1);
//        } catch (SQLException ex) {
//            throw new DatabaseUnavailableException("Database unavailable");
//        }
//        return idExRate;
//    }
}
