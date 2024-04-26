package dao;

import model.Currency;
import model.ExchangeRates;
import utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public final class RequestDb {
    /**
     * Метод для получения валюты по заданному коду.
     * Example: GET /currency/EUR
     */
    public static Currency getCurrencyByCode(String code) {
        String sql = """
                SELECT *
                FROM Currencies 
                WHERE code = ?""";

        Currency curr = new Currency();

        try (
                Connection con = ConnectionManager.open();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();

            curr = new Currency(rs.getInt("id"),
                    rs.getString("code"),
                    rs.getString("full_name"),
                    rs.getString("sign"));
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return curr;
    }

    /**
     * Метод для получения всех валют из таблицы Currencies,
     * для GET /currencies
     */
    public static List<Currency> getAllCurrencies() {
        String sql = """
                SELECT *
                FROM Currencies""";
        List<Currency> currencies = new ArrayList<>();
        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                currencies.add(new Currency(rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("full_name"),
                        rs.getString("sign")));
            }

        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return currencies;
    }

    /**
     * Метод для добавления в таблицу новой валюты,
     * для POST /currencies (code, name and sign передаются в теле запроса)
     */
    public static void addCurrency(Currency curr) {
        if (getCurrencyByCode(curr.getCode()).getId() != 0) {
            System.out.println("Такая валюта уже существует");
        } else {
            String sql = """
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
                System.out.println("Валюта успешно добавлена");

            } catch (SQLException ex) {
                throw new RuntimeException("Валюта не добавлена");
            }
        }
    }

    /**
     * Метод, который должен в каком-то виде, возвращать все обменные курсы.
     * Как это организовать я пока не понимаю
     * Этот метод предназначен для запроса: GET /exchangeRates
     */
    public static List<ExchangeRates> getAllExchangeRates() {
        //придумать, как можно вместо c.code получать всю информацию о валюте, а
        //не только код
        String sql = """
                SELECT ex.id,
                       (SELECT c.code
                        FROM Currencies as c
                        WHERE id = ex.base_currency_id) as base,
                       (SELECT c.code
                        FROM Currencies as c
                        WHERE id = ex.target_currency_id) as target,
                        ex.rate
                FROM ExchangeRates as ex""";

        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {

        } catch (SQLException ex) {

        }

        return List.of(); //это заглушка, чтобы метод не ругался
    }

    /**
     * Метод для получения по кодам 2 валют их обменный курс: id курса,
     * код стартовой валюты, код конечной валюты, ставка.
     * Использоваться будет для запроса: GET /exchangeRate/USDRUB
     */
    public static ExchangeRates getExchangeRateByCode(String baseCode, String targetCode) {
        int idExRate = getIdExRate(baseCode, targetCode);
        ReceivedRate rate = new ReceivedRate(
                RequestDb.getCurrencyByCode(baseCode).getCode(),
                RequestDb.getCurrencyByCode(targetCode).getCode()
        );

        ExchangeRates rates = new ExchangeRates(idExRate,
                RequestDb.getCurrencyByCode(baseCode),
                RequestDb.getCurrencyByCode(targetCode),
                rate.translate()
        );


        return rates;
    }

    /**
     * Метод для получения id пары обменного курса из одной валюты в другую
     */
    private static int getIdExRate(String baseCode, String targetCode) {
        int idExRate;

        String sql = """
                SELECT id
                FROM ExchangeRates
                WHERE base_currency_id = ? and
                target_currency_id = ?""";


        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, getCurrencyByCode(baseCode).getId());
            stmt.setInt(2, getCurrencyByCode(targetCode).getId());
            idExRate = stmt.executeQuery().getInt(1);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return idExRate;
    }

    /**
     * Метод, который получает на вход коды валют и ставку, а потом добавляет
     * эти данные в таблицу ExchangeRates.
     * Метод написан под запрос: POST /exchangeRates
     * */
    public static void addExchangeRates(String codeStartCurrency, String codeEndCurrency,
                                 int rate) {
        String sql = """
                    INSERT INTO ExchangeRates(base_currency_id, 
                    target_currency_id, rate)
                    VALUES (?, ?, ?)""";

        try(
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            int idStart = getCurrencyByCode(codeStartCurrency).getId();
            int idEnd = getCurrencyByCode(codeEndCurrency).getId();
            if ((idStart == idEnd) || (idStart == 0 || idEnd == 0)) {
                throw new RuntimeException("Таких валют не существует");
            }
            statement.setInt(1, idStart);
            statement.setInt(2, idEnd);
            statement.setInt(3, rate);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Не получилось добавить новый обменный курс");
        }
    }

    /**
     * Метод, который получает на вход коды валют и ставку, а потом обновляет
     * эти данные в таблице ExchangeRates.
     * Метод написан под запрос: PATCH /exchangeRate/USDRUB
     * */
    public static void updateExchangeRates(String codeStartCurrency, String codeEndCurrency,
                                 int rate) {
        String sql = """
                UPDATE ExchangeRates
                SET rate = ?
                WHERE base_currency_id = ? AND
                      target_currency_id = ?""";
        String sqlHelper = """
                    SELECT count(*)
                    FROM ExchangeRates
                    WHERE base_currency_id = 1 AND
                          target_currency_id = 3""";

        try(
                Connection connection = ConnectionManager.open();
                PreparedStatement statement1 = connection.prepareStatement(sqlHelper);
                PreparedStatement statement2 = connection.prepareStatement(sql);
        ) {
            int idStart = getCurrencyByCode(codeStartCurrency).getId();
            int idEnd = getCurrencyByCode(codeEndCurrency).getId();
            if (statement1.executeQuery().getInt(1) == 1) {
                statement2.setInt(1, rate);
                statement2.setInt(2, idStart);
                statement2.setInt(3, idEnd);
                statement2.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Не получилось обновить обменный курс");
        }
    }
}
