package dao;

import model.Currency;
import model.ExchangeRate;
import utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
                SELECT ex.id,
                       (SELECT c.code
                        FROM Currencies as c
                        WHERE c.id = ex.base_currency_id) as base,
                       (SELECT c.code
                        FROM Currencies as c
                        WHERE c.id = ex.target_currency_id) as target,
                        ex.rate
                FROM ExchangeRates as ex""";
        List<ExchangeRate> list = new ArrayList<>();
        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.executeQuery();
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                list.add(getExchangeRate(resultSet));
            }
        } catch (SQLException ex) {
            //TODO
        }

        return list;
    }

    /**
     * Метод для получения по кодам 2 валют их обменный курс: id курса,
     * код стартовой валюты, код конечной валюты, ставка.
     * Использоваться будет для запроса: GET /exchangeRate/USDRUB
     */
    @Override
    public Optional<ExchangeRate> findByCode(String baseCode, String targetCode) {
        int idExRate = getId(baseCode, targetCode);
        JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();

        ReceivedRate rate = new ReceivedRate(
                jdbcCurrencyDao.findByCode(baseCode).orElse(null).getCode(),
                jdbcCurrencyDao.findByCode(targetCode).orElse(null).getCode()
        );

        return Optional.of(new ExchangeRate(idExRate,
                jdbcCurrencyDao.findByCode(baseCode).orElse(null),
                jdbcCurrencyDao.findByCode(targetCode).orElse(null),
                rate.translate()
        ));
    }

    /**
     * Метод, который получает на вход коды валют и ставку, а потом добавляет
     * эти данные в таблицу ExchangeRate.
     * Метод написан под запрос: POST /exchangeRates
     */
    //в слое выше стоит из кодов валют и ставки создавать объект класса ExchangeRate
    @Override
    public Optional<ExchangeRate> save(ExchangeRate exchangeRate) {
        final String sql = """
                INSERT INTO ExchangeRates(base_currency_id, 
                target_currency_id, rate)
                VALUES (?, ?, ?)""";

        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            int idStart = exchangeRate.getBaseCurrency().getId();
            int idEnd = exchangeRate.getTargetCurrency().getId();
            if ((idStart == idEnd) || (idStart == 0 || idEnd == 0)) {
                throw new RuntimeException("Таких валют не существует");
            }
            statement.setInt(1, idStart);
            statement.setInt(2, idEnd);
            statement.setDouble(3, exchangeRate.getRate());
            ResultSet rs = statement.executeQuery();

            return Optional.of(getExchangeRate(rs));
        } catch (SQLException ex) {
            //throw new RuntimeException("Не получилось добавить новый обменный курс");
        }
        return Optional.empty();
    }

    /**
     * Метод, который получает на вход коды валют и ставку, а потом обновляет
     * эти данные в таблице ExchangeRate.
     * Метод написан под запрос: PATCH /exchangeRate/USDRUB
     */
    //в слое выше стоит из кодов валют и ставки создавать объект класса ExchangeRate
    @Override
    public Optional<ExchangeRate> update(ExchangeRate exchangeRate) {
        final String sql = """
                UPDATE ExchangeRates
                SET rate = ?
                WHERE base_currency_id = ? AND
                      target_currency_id = ?""";

        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement2 = connection.prepareStatement(sql);
        ) {
            int idStart = exchangeRate.getBaseCurrency().getId();
            int idEnd = exchangeRate.getTargetCurrency().getId();

            statement2.setDouble(1, exchangeRate.getRate());
            statement2.setInt(2, idStart);
            statement2.setInt(3, idEnd);
            ResultSet rs = statement2.executeQuery();

            return Optional.of(getExchangeRate(rs));

        } catch (SQLException ex) {
            //throw new RuntimeException("Не получилось обновить обменный курс");
        }
        return Optional.empty();
    }

    /**
     * Делаем запрос к таблице ExchangeRate и возвращаем rate
     */
    public double getRate(int idStartCurrency, int idEndCurrency) {
        String sql = """
                SELECT rate
                FROM ExchangeRates
                WHERE base_currency_id = ?
                and target_currency_id = ?""";
        double rate;

        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setDouble(1, idStartCurrency);
            statement.setDouble(2, idEndCurrency);
            ResultSet rs = statement.executeQuery();
            rate = rs.getDouble("rate");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rate;
    }

    private ExchangeRate getExchangeRate(ResultSet resultSet) {
        JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();
        int id;
        ExchangeRate exchangeRate;
        try {
            id = resultSet.getInt(1);


            Currency currency1 = jdbcCurrencyDao.
                    findByCode(resultSet.getString(2))
                    .orElse(null);

            Currency currency2 = jdbcCurrencyDao
                    .findByCode(resultSet.getString(3))
                    .orElse(null);

            double rate = resultSet.getDouble(4);
            exchangeRate = new ExchangeRate(
                    id,
                    currency1,
                    currency2,
                    rate
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exchangeRate;
    }

    /**
     * Метод для получения id пары обменного курса из одной валюты в другую
     */
    private int getId(String baseCode, String targetCode) {
        int idExRate;

        final String sql = """
                SELECT id
                FROM ExchangeRates
                WHERE base_currency_id = ? and
                target_currency_id = ?""";


        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();
            stmt.setInt(1,
                    jdbcCurrencyDao.findByCode(baseCode).orElse(null).getId());
            stmt.setInt(2,
                    jdbcCurrencyDao.findByCode(targetCode).orElse(null).getId());
            idExRate = stmt.executeQuery().getInt(1);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return idExRate;
    }
}
