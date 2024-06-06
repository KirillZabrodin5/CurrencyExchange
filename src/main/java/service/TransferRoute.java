package service;

import Exceptions.DatabaseUnavailableException;
import Exceptions.NotFoundException;
import dao.JdbcCurrencyDao;
import dao.JdbcExchangeRateDao;
import entities.Currency;

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
    public double getRate() {
        double answer;

        if (idStartCurrency.equals(idEndCurrency)) {
            return 1;
        }

        double result = exchangeRateDao.getRate(idStartCurrency, idEndCurrency);
        if (result > 0) {
            //если есть прямой перевод, то работаем
            answer = result;
        } else {
            //если прямого перевода нет, то 2 случая:
            //есть перевод BA и есть перевод с промежуточной валютой USD
            result = exchangeRateDao.getRate(idEndCurrency, idStartCurrency);
            if (result > 0) {
                //BA
                answer = 1 / result;
            } else {
                //перевод с промежуточной валютой USD
                answer = transferWithIntermediateCurrency();
            }
        }

        return answer;
    }

    private double transferWithIntermediateCurrency() {
        Currency currency = currencyDao.findByCode(TRANSIT_CODE_CURRENCY).orElseThrow();
        Long idUsd = currency.getId();

        double USDtoStart = exchangeRateDao
                .getRate(idUsd, idStartCurrency);

        double USDtoEnd = exchangeRateDao
                .getRate(idUsd, idEndCurrency);

        return USDtoEnd / USDtoStart;
    }
}
