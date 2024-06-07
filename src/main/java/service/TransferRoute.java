package service;

import Exceptions.DatabaseUnavailableException;
import Exceptions.NotFoundException;
import dao.JdbcCurrencyDao;
import dao.JdbcExchangeRateDao;
import entities.Currency;

import java.math.BigDecimal;

/**
 * Класс для определения, существует ли прямой маршрут перевода,
 * обратный курс или такого курса нет вообще
 * и вызывает соответствующую реализацию.
 * Основой метод класса - translate, с помощью него
 * определяем маршрут перевода и возвращаем rate
 */
public class TransferRoute { //название говно, подумать еще
    private final Long idStartCurrency;
    private final Long idEndCurrency;
    private final JdbcCurrencyDao currencyDao = new JdbcCurrencyDao();
    private final JdbcExchangeRateDao exchangeRateDao = new JdbcExchangeRateDao();
    private final static String TRANSIT_CODE_CURRENCY = "USD";

    public TransferRoute(String startCodeCurrency, String endCodeCurrency)
            throws NotFoundException, DatabaseUnavailableException {
        idStartCurrency = currencyDao
                .findByCode(startCodeCurrency)
                .orElseThrow()
                .getId();
        idEndCurrency = currencyDao
                .findByCode(endCodeCurrency)
                .orElseThrow()
                .getId();
    }

    /**
     * Этот метод возвращает rate. Например, если из 63.75 рублей хотим получить доллары, то получим один
     */
    public BigDecimal getRate() {
        BigDecimal answer;

        if (idStartCurrency.equals(idEndCurrency)) {
            answer = new BigDecimal(1);
            return answer;
        }

        BigDecimal result = exchangeRateDao.getRate(idStartCurrency, idEndCurrency);
        if (result.compareTo(new BigDecimal(0)) > 0) {
            //если есть прямой перевод, то работаем
            answer = result;
        } else {
            //если прямого перевода нет, то 2 случая:
            //есть перевод BA и есть перевод с промежуточной валютой USD
            result = exchangeRateDao.getRate(idEndCurrency, idStartCurrency);
            if (result.compareTo(new BigDecimal(0)) > 0) {
                //BA
                answer = new BigDecimal(1).divide(result);
            } else {
                //перевод с промежуточной валютой USD
                answer = transferWithIntermediateCurrency();
            }
        }

        return answer;
    }

    private BigDecimal transferWithIntermediateCurrency() {
        Currency currency = currencyDao.findByCode(TRANSIT_CODE_CURRENCY).orElseThrow();
        Long idUsd = currency.getId();

        BigDecimal USDtoStart = exchangeRateDao
                .getRate(idUsd, idStartCurrency);

        BigDecimal USDtoEnd = exchangeRateDao
                .getRate(idUsd, idEndCurrency);

        return USDtoEnd.divide(USDtoStart);
    }
}
