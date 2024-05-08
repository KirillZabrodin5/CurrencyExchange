package dto;

import Exceptions.DatabaseUnavailableException;
import Exceptions.EntityExistsException;
import Exceptions.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dao.JdbcCurrencyDao;
import model.Currency;

import java.util.List;

/**
 * Класс для преобразования SQL запроса в Json
 */
public class CurrencyDto {
    private final static ObjectMapper mapper = new ObjectMapper();
    private final static JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();

    public String getAllJson() {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            List<Currency> currencies = jdbcCurrencyDao.findAll();
            try {
                return mapper.writeValueAsString(currencies);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } catch (DatabaseUnavailableException exception) {
            String message = exception.getMessage();
            ObjectNode json = mapper.createObjectNode();
            json.put("message", message);
            return json.toString();
        }
    }

    public String getJson(String code) {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            Currency currency = jdbcCurrencyDao.findByCode(code).orElseThrow();
            try {
                return mapper.writeValueAsString(currency);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } catch (DatabaseUnavailableException | NotFoundException exception) {
            String message = exception.getMessage();
            ObjectNode json = mapper.createObjectNode();
            json.put("message", message);
            return json.toString();
        }
    }

    public String addJson(String code, String name, String sign) {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            Currency currency = jdbcCurrencyDao.save(new Currency(code, name, sign)).orElseThrow();
            try {
                return mapper.writeValueAsString(currency);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } catch (DatabaseUnavailableException | EntityExistsException exception) {
            String message = exception.getMessage();
            ObjectNode json = mapper.createObjectNode();
            json.put("message", message);
            return json.toString();
        }
    }
}
