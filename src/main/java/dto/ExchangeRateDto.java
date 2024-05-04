package dto;

import Exceptions.DatabaseUnavailableException;
import Exceptions.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.JdbcCurrencyDao;
import dao.JdbcExchangeRateDao;
import model.Currency;
import model.ExchangeRate;

import java.util.List;

public class ExchangeRateDto {
    ObjectMapper mapper = new ObjectMapper();
    JdbcExchangeRateDao jdbcExchangeRateDao = new JdbcExchangeRateDao();
    JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();

    public String getJsonAllExchangeRate() {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        List<ExchangeRate> exchangeRates = jdbcExchangeRateDao.findAll();
        try {
            return mapper.writeValueAsString(exchangeRates);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getJsonExchangeRate(String exchangeTwoCode) {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String baseCode = exchangeTwoCode.substring(0, 3);
        String targetCode = exchangeTwoCode.substring(3, 6);
        ExchangeRate exchangeRate = jdbcExchangeRateDao.findByCode(baseCode,
                targetCode).orElseThrow(
                () -> new DatabaseUnavailableException("database unanailable")
        );
        try{
            return mapper.writeValueAsString(exchangeRate);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String saveExchangeRate(String baseCode, String targetCode, double rate) {
        Currency baseCurrency = jdbcCurrencyDao.findByCode(baseCode).orElseThrow(
                () -> new NotFoundException("element not found")
        );
        Currency targetCurrency = jdbcCurrencyDao.findByCode(targetCode).orElseThrow(
                () -> new NotFoundException("element not found")
        );
        ExchangeRate inputExchangeRate = new ExchangeRate(baseCurrency, targetCurrency, rate);

        ExchangeRate outputExchangeRate = jdbcExchangeRateDao.save(inputExchangeRate).orElseThrow(
                () -> new DatabaseUnavailableException("database unanailable")
        );

        try {
            return mapper.writeValueAsString(outputExchangeRate);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String updateExchangeRate(String exchangeTwoCode, double rate) {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String baseCode = exchangeTwoCode.substring(0, 3);
        String targetCode = exchangeTwoCode.substring(3, 6);
        ExchangeRate inputExchangeRate = jdbcExchangeRateDao.findByCode(baseCode,
                targetCode).orElseThrow(
                () -> new DatabaseUnavailableException("database unanailable")
        );
        inputExchangeRate.setRate(rate);
        ExchangeRate outputExchangeRate = jdbcExchangeRateDao.update(inputExchangeRate).orElseThrow(
                () -> new DatabaseUnavailableException("database unanailable")
        );
        try{
            return mapper.writeValueAsString(outputExchangeRate);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
