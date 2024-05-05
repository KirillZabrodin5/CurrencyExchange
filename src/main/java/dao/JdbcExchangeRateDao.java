package dao;

import Exceptions.DatabaseUnavailableException;
import Exceptions.NotFoundException;
import model.Currency;
import model.CurrencyExchange;
import model.ExchangeRate;
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
        Long idExRate = getId(baseCode, targetCode);
        JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();

        CurrencyExchange rate = new CurrencyExchange(baseCode, targetCode);

        return Optional.of(new ExchangeRate(idExRate,
                jdbcCurrencyDao.findByCode(baseCode).get(),
                jdbcCurrencyDao.findByCode(targetCode).get(),
                rate.translate()
        ));
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
    public Optional<ExchangeRate> save(ExchangeRate exchangeRate) {
        final String sql = """
                INSERT INTO ExchangeRates(base_currency_id, 
                target_currency_id, rate)
                VALUES (?, ?, ?)
                RETURNING id, base_currency_id, 
                target_currency_id, rate""";

        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            Long idStart = exchangeRate.getBaseCurrency().getId();
            Long idEnd = exchangeRate.getTargetCurrency().getId();
            if ((Objects.equals(idStart, idEnd)) || (idStart == 0 || idEnd == 0)) {
                throw new NotFoundException("Currencies not found");
            }

            statement.setLong(1, idStart);
            statement.setLong(2, idEnd);
            statement.setDouble(3, exchangeRate.getRate());
            ResultSet rs = statement.executeQuery();

            return Optional.of(getExchangeRate(rs));
        } catch (SQLException ex) {
            //todo выбрасывание exception в случае существования уже заданной пары валют
            //инфа, как это сделать, есть в 3 лекции расширенного роадмапа
            //тут надо смотреть на то, что отвечает SQLite
            throw new DatabaseUnavailableException("Database unavailable");
        }
    }

    @Override
    public Optional<ExchangeRate> delete(ExchangeRate exchangeRate) {
        final String sql = """
                DELETE FROM ExchangeRate
                WHERE base_currency_id = ? AND
                target_currency_id = ?""";
        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, exchangeRate.getBaseCurrency().getId());
            statement.setLong(2, exchangeRate.getTargetCurrency().getId());
            ResultSet rs = statement.executeQuery();
            return Optional.of(getExchangeRate(rs));
        } catch (SQLException ex) {
            //todo выбрасывание exception в случае не существования заданной пары валют
            //инфа, как это сделать, есть в 3 лекции расширенного роадмапа
            //тут надо смотреть на то, что отвечает SQLite
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
    //в слое выше стоит из кодов валют и ставки создавать объект класса ExchangeRate
    @Override
    public Optional<ExchangeRate> update(ExchangeRate exchangeRate) {
        final String sql = """
                UPDATE ExchangeRates
                SET rate = ?
                WHERE base_currency_id = ? AND
                      target_currency_id = ?
                RETURNING id, base_currency_id, 
                target_currency_id, rate""";

        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement2 = connection.prepareStatement(sql);
        ) {
            Long idStart = exchangeRate.getBaseCurrency().getId();
            if (idStart == 0L) {
                throw new NotFoundException("Currency not found");
            }
            Long idEnd = exchangeRate.getTargetCurrency().getId();
            if (idEnd == 0L) {
                throw new NotFoundException("Currency not found");
            }

            statement2.setDouble(1, exchangeRate.getRate());
            statement2.setLong(2, idStart);
            statement2.setLong(3, idEnd);
            ResultSet rs = statement2.executeQuery();

            return Optional.of(getExchangeRate(rs));
        } catch (SQLException ex) {
            //todo выбрасывание exception в случае не существования заданной пары валют
            //инфа, как это сделать, есть в 3 лекции расширенного роадмапа
            //тут надо смотреть на то, что отвечает SQLite
            throw new DatabaseUnavailableException("Database unavailable");
        }

    }

    /**
     * Делаем запрос к таблице ExchangeRate и возвращаем rate
     */
    public double getRate(Long idStartCurrency, Long idEndCurrency) {
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
            JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();
            jdbcCurrencyDao.findById(idStartCurrency).orElseThrow(
                    () -> new NotFoundException("Currency not found")
            );
            jdbcCurrencyDao.findById(idEndCurrency).orElseThrow(
                    () -> new NotFoundException("Currency not found")
            );
            statement.setDouble(1, idStartCurrency);
            statement.setDouble(2, idEndCurrency);
            ResultSet rs = statement.executeQuery();
            rate = rs.getDouble("rate");
        } catch (SQLException e) {
            //todo выбрасывание exception в случае не существования заданной пары валют
            //инфа, как это сделать, есть в 3 лекции расширенного роадмапа
            //тут надо смотреть на то, что отвечает SQLite
            throw new DatabaseUnavailableException("Database unavailable");
        }
        return rate;
    }

    private ExchangeRate getExchangeRate(ResultSet resultSet) {
        JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();
        ExchangeRate exchangeRate;
        try {
            Long id = resultSet.getLong(1);
            if (id == 0L) {
                throw new NotFoundException("Currency not found");
            }

            Currency currency1 = jdbcCurrencyDao.
                    findById(resultSet.getLong(2))
                    .orElse(null);

            Currency currency2 = jdbcCurrencyDao.
                    findById(resultSet.getLong(3))
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
    private Long getId(String baseCode, String targetCode) {
        Long idExRate;

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
            stmt.setLong(1,
                    jdbcCurrencyDao.findByCode(baseCode).orElseThrow(
                            () -> new NotFoundException("Currency not found")).getId());
            stmt.setLong(2,
                    jdbcCurrencyDao.findByCode(targetCode).orElseThrow(
                            () -> new NotFoundException("Currency not found")).getId());
            idExRate = stmt.executeQuery().getLong(1);
        } catch (SQLException ex) {
            throw new DatabaseUnavailableException("Database unavailable");
        }
        return idExRate;
    }
}
