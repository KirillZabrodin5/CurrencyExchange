//package servlet;
//
//import Exceptions.DatabaseUnavailableException;
//import Exceptions.EntityExistsException;
//import Exceptions.InvalidParameterException;
//import Exceptions.NotFoundException;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import dao.JdbcCurrencyDao;
//import dao.JdbcExchangeRateDao;
//import model.Currency;
//import model.ExchangeRate;
//
//import java.util.List;
//
////это не DTO класс - это должны делать сервлеты
//
//public class ExchangeRateDto {
//    ObjectMapper mapper = new ObjectMapper();
//    JdbcExchangeRateDao jdbcExchangeRateDao = new JdbcExchangeRateDao();
//    JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();
//
//    public String getAllJson() {
//        mapper.enable(SerializationFeature.INDENT_OUTPUT);
//        try {
//            List<ExchangeRate> exchangeRates = jdbcExchangeRateDao.findAll();
//            try {
//                return mapper.writeValueAsString(exchangeRates);
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }
//        } catch (DatabaseUnavailableException e) {
//            String message = e.getMessage();
//            ObjectNode json = mapper.createObjectNode();
//            json.put("message", message);
//            return json.toString();
//        }
//    }
//
//    public String getJson(String exchangeTwoCode) {
//        mapper.enable(SerializationFeature.INDENT_OUTPUT);
//        String baseCode;
//        String targetCode;
//        try {
//            baseCode = exchangeTwoCode.substring(0, 3);
//            targetCode = exchangeTwoCode.substring(3, 6);
//        } catch (StringIndexOutOfBoundsException e) {
//            String message = "Currency code pairs missing at address";
//            ObjectNode json = mapper.createObjectNode();
//            json.put("message", message);
//            return json.toString();
//        }
//        try {
//            ExchangeRate exchangeRate = jdbcExchangeRateDao.findByCode(baseCode, targetCode).orElseThrow();
//            try {
//                return mapper.writeValueAsString(exchangeRate);
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }
//        } catch (DatabaseUnavailableException | NotFoundException e) {
//            String message = e.getMessage();
//            ObjectNode json = mapper.createObjectNode();
//            json.put("message", message);
//            return json.toString();
//        }
//    }
//
//    public String save(String baseCode, String targetCode, double rate) {
//        mapper.enable(SerializationFeature.INDENT_OUTPUT);
//
//        try {
//            if (!(baseCode.length() == 3 && targetCode.length() == 3)) {
//                throw new InvalidParameterException("Currency code pairs missing at address");
//            }
//        } catch (InvalidParameterException e) {
//            String message = e.getMessage();
//            ObjectNode json = mapper.createObjectNode();
//            json.put("message", message);
//            return json.toString();
//        }
//
//        try {
//            Currency baseCurrency = jdbcCurrencyDao.findByCode(baseCode).orElseThrow();
//            Currency targetCurrency = jdbcCurrencyDao.findByCode(targetCode).orElseThrow();
//            ExchangeRate inputExchangeRate = new ExchangeRate(baseCurrency, targetCurrency, rate);
//            ExchangeRate outputExchangeRate = jdbcExchangeRateDao.save(inputExchangeRate).orElseThrow();
//
//            try {
//                return mapper.writeValueAsString(outputExchangeRate);
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }
//        } catch (DatabaseUnavailableException | NotFoundException | EntityExistsException e) {
//            String message = e.getMessage();
//            ObjectNode json = mapper.createObjectNode();
//            json.put("message", message);
//            return json.toString();
//        }
//    }
//
//    public String update(String exchangeTwoCode, double rate) {
//        mapper.enable(SerializationFeature.INDENT_OUTPUT);
//        String baseCode;
//        String targetCode;
//        try {
//            baseCode = exchangeTwoCode.substring(0, 3);
//            targetCode = exchangeTwoCode.substring(3, 6);
//        } catch (StringIndexOutOfBoundsException e) {
//            String message = "Currency code pairs missing at address";
//            ObjectNode json = mapper.createObjectNode();
//            json.put("message", message);
//            return json.toString();
//        }
//        try {
//            ExchangeRate inputExchangeRate = jdbcExchangeRateDao.findByCode(baseCode, targetCode).orElseThrow();
//            inputExchangeRate.setRate(rate);
//            ExchangeRate outputExchangeRate = jdbcExchangeRateDao.update(inputExchangeRate).orElseThrow();
//            try {
//                return mapper.writeValueAsString(outputExchangeRate);
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }
//        } catch (DatabaseUnavailableException | NotFoundException e) {
//            String message = e.getMessage();
//            ObjectNode json = mapper.createObjectNode();
//            json.put("message", message);
//            return json.toString();
//        }
//    }
//}
