package service;

import Exceptions.DatabaseUnavailableException;
import Exceptions.NotFoundException;
import dao.JdbcCurrencyDao;
import dao.JdbcExchangeRateDao;
import dto.CurrencyDto;
import model.Currency;
import utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Exchange {
    private final Long idStartCurrency;
    private final Long idEndCurrency;
    private final JdbcExchangeRateDao jdbcExchangeRateDao = new JdbcExchangeRateDao();

    public Exchange(Long idStartCurrency, Long idEndCurrency) {
        this.idStartCurrency = idStartCurrency;
        this.idEndCurrency = idEndCurrency;
    }

    public double translate() {
        double answer;

        if (idStartCurrency.equals(idEndCurrency)) {
            return 1;
        }

        String sql = """
                SELECT rate
                FROM ExchangeRates
                WHERE base_currency_id = ?
                and target_currency_id = ?
                """;
        try (
                Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setLong(1, idStartCurrency);
            statement.setLong(2, idEndCurrency);
            ResultSet rs = statement.executeQuery();
            double result = rs.getInt(1);

            if (result > 0) {
                //если есть прямой перевод, то работаем
                answer = result;
            } else {
                //если прямого перевода нет, то 3 случая:
                //есть перевод BA и есть перевод с промежуточной валютой USD, и перевод невозможен,
                //то есть нет даже перевода с промежуточной валютой
                statement.setLong(1, idEndCurrency);
                statement.setLong(2, idStartCurrency);
                rs = statement.executeQuery();
                result = rs.getInt(1);
                if (result > 0) {
                    //BA
                    answer = 1 / result;
                } else {
                    //перевод с промежуточной валютой USD
                    answer = transferWithIntermediateCurrency();
                }
            }
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Database unavailable");
        }
        return answer;
    }

    private double transferWithIntermediateCurrency() {
        JdbcCurrencyDao dao = new JdbcCurrencyDao();
        CurrencyDto currencyDto = new CurrencyDto("USD");
        Currency currency = dao.findByCode(currencyDto).orElseThrow();
        Long idUsd = currency.getId();

        double USDtoStart = jdbcExchangeRateDao
                .getRate(idUsd, idStartCurrency);

        double USDtoEnd = jdbcExchangeRateDao
                .getRate(idUsd, idEndCurrency);
        return USDtoEnd / USDtoStart;
    }
}
