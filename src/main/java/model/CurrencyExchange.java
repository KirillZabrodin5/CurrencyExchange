package model;

import Exceptions.DatabaseUnavailableException;
import Exceptions.NotFoundException;
import dao.JdbcCurrencyDao;
import dao.JdbcExchangeRateDao;
import dto.CurrencyDto;
import utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

/**
 * Класс для определения, существует ли прямой маршрут перевода,
 * обратный курс или такого курса нет вообще
 * и вызывает соответствующую реализацию.
 * Основой метод класса - translate, с помощью него
 * определяем маршрут перевода и возвращаем rate
 */
public class CurrencyExchange {
//    private final Long idStartCurrency;
//    private final Long idEndCurrency;
//    private final JdbcExchangeRateDao jdbcExchangeRateDao = new JdbcExchangeRateDao();
//
//    public CurrencyExchange(String startCodeCurrency, String endCodeCurrency) throws NotFoundException,
//            DatabaseUnavailableException {
//        JdbcCurrencyDao dao = new JdbcCurrencyDao();
//        CurrencyDto currencyDtoStart = new CurrencyDto(startCodeCurrency);
//        CurrencyDto currencyDtoEnd = new CurrencyDto(endCodeCurrency);
//        idStartCurrency = dao
//                .findByCode(currencyDtoStart)
//                .orElseThrow()
//                .getId();
//        idEndCurrency = dao
//                .findByCode(currencyDtoEnd)
//                .orElseThrow()
//                .getId();
//    }

    private Currency baseCurrency;
    private Currency targetCurrency;
    private String rate;
    private String amount;
    private String convertedAmount;

    public CurrencyExchange(Currency baseCurrency, Currency targetCurrency,
                            double rate, double amount) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        DecimalFormat decFormat = new DecimalFormat("#.##");
        this.rate = decFormat.format(rate).replace(',', '.');
        double rateD = Double.parseDouble(this.rate);
        this.amount = decFormat.format(amount).replace(',', '.');
        double amountD = Double.parseDouble(this.amount);
        this.convertedAmount = decFormat.format(amountD * rateD).replace(',', '.');
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(Currency targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(String convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    @Override
    public String toString() {
        return "CurrencyExchange{" +
                "baseCurrency=" + baseCurrency +
                ", targetCurrency=" + targetCurrency +
                ", rate='" + rate + '\'' +
                ", amount='" + amount + '\'' +
                ", convertedAmount='" + convertedAmount + '\'' +
                '}';
    }
}
