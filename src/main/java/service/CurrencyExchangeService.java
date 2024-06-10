package service;

import Exceptions.NotFoundException;
import dao.JdbcCurrencyDao;
import dao.JdbcExchangeRateDao;
import dto.CurrencyExchangeDto;
import entity.Currency;
import utils.ConverterUtil;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Класс для определения, существует ли прямой маршрут перевода,
 * обратный курс или такого курса нет вообще
 * и вызывает соответствующую реализацию.
 * Основой метод класса - translate, с помощью него
 * определяем маршрут перевода и возвращаем rate
 */

public class CurrencyExchangeService {
    private Long idStartCurrency;
    private Long idEndCurrency;
    private final JdbcCurrencyDao currencyDao = new JdbcCurrencyDao();
    private final JdbcExchangeRateDao exchangeRateDao = new JdbcExchangeRateDao();
    private final static String TRANSIT_CODE_CURRENCY = "USD";
    private static final ConverterUtil CONVERTER_UTIL = new ConverterUtil();

    public CurrencyExchangeService() {
    }

    public CurrencyExchangeDto getCurrencyExchange(CurrencyExchangeDto currencyExchangeDto) {
        Currency baseCurrency = CONVERTER_UTIL.dtoToCurrency(currencyExchangeDto.getBaseCurrency());
        Currency targetCurrency = CONVERTER_UTIL.dtoToCurrency(currencyExchangeDto.getTargetCurrency());

        idStartCurrency = baseCurrency.getId();
        idEndCurrency = targetCurrency.getId();

        BigDecimal rate = getRate();
        return new CurrencyExchangeDto(currencyExchangeDto.getBaseCurrency(),
                currencyExchangeDto.getTargetCurrency(),
                rate, currencyExchangeDto.getAmount());
    }

    private BigDecimal getRate() {
        BigDecimal answer;

        if (idStartCurrency.equals(idEndCurrency)) {
            answer = new BigDecimal(1);
            return answer;
        }

        BigDecimal result;
        try {
            result = exchangeRateDao.getRate(idStartCurrency, idEndCurrency);
            //если есть прямой перевод, то работаем
            answer = result;
        } catch (NotFoundException ex) {
            //если прямого перевода нет, то 2 случая:
            //есть перевод BA и есть перевод с промежуточной валютой USD
            try {
                result = exchangeRateDao.getRate(idEndCurrency, idStartCurrency);
                //BA
                answer = new BigDecimal(1).divide(result, MathContext.DECIMAL128);
            } catch (NotFoundException e) {
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

        return USDtoEnd.divide(USDtoStart, MathContext.DECIMAL128);
    }
}
