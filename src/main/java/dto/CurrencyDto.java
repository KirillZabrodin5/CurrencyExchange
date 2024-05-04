package dto;

import Exceptions.EntityExistsException;
import Exceptions.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.JdbcCurrencyDao;
import model.Currency;

import java.io.IOException;
import java.util.List;

/**
 * Класс для преобразования SQL запроса в Json
 * */
public class CurrencyDto {
    private final static ObjectMapper mapper = new ObjectMapper();
    private final static JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();

    public String getJsonAllCurrencies() {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        List<Currency> currencies = jdbcCurrencyDao.findAll();
        try {
            return mapper.writeValueAsString(currencies);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getJsonCurrency(String code) {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        Currency currency = jdbcCurrencyDao.findByCode(code).orElseThrow(
                () -> new NotFoundException("element not found")
        );
        try {
            return mapper.writeValueAsString(currency);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String addJsonCurrency(String code, String name, String sign) {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        jdbcCurrencyDao.save(new Currency(code, name, sign));
        Currency currency = jdbcCurrencyDao.findByCode(code).orElseThrow(
                () -> new EntityExistsException("element already exists in database")
        );
        try{
            return mapper.writeValueAsString(currency);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
