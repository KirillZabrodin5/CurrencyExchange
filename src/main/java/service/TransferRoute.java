package service;

import Exceptions.DatabaseUnavailableException;
import Exceptions.NotFoundException;
import dao.JdbcCurrencyDao;
import dao.JdbcExchangeRateDao;
import dto.CurrencyDto;
import model.Currency;

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
    private final JdbcExchangeRateDao jdbcExchangeRateDao = new JdbcExchangeRateDao();

    public TransferRoute(String startCodeCurrency, String endCodeCurrency) throws NotFoundException, DatabaseUnavailableException {
        JdbcCurrencyDao dao = new JdbcCurrencyDao();
        CurrencyDto currencyDtoStart = new CurrencyDto(startCodeCurrency);
        CurrencyDto currencyDtoEnd = new CurrencyDto(endCodeCurrency);
        idStartCurrency = dao
                .findByCode(currencyDtoStart)
                .orElseThrow()
                .getId();
        idEndCurrency = dao
                .findByCode(currencyDtoEnd)
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

        double result = jdbcExchangeRateDao.getRate(idStartCurrency, idEndCurrency);
        if (result > 0) {
            //если есть прямой перевод, то работаем
            answer = result;
        } else {
            //если прямого перевода нет, то 2 случая:
            //есть перевод BA и есть перевод с промежуточной валютой USD
            result = jdbcExchangeRateDao.getRate(idEndCurrency, idStartCurrency);
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
