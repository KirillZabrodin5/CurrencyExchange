package dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.JdbcCurrencyDao;
import model.Currency;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Класс для преобразования SQL запроса в Json
 * */
public class CurrencyDTO {
    public void getJsonAllCurrencies() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            File file = new File("resources/jsonCur.json");
            JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();
            List<Currency> currencies = jdbcCurrencyDao.getAllCurrencies();

            mapper.writeValue(file, currencies);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getJsonCurrency(String code) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            File file = new File("resources/jsonCurrency.json");
            JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();
            Currency currency = jdbcCurrencyDao.getCurrencyByCode(code);

            mapper.writeValue(file, currency);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
