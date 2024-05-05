package dto;

import Exceptions.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.JdbcCurrencyDao;
import model.Currency;
import model.CurrencyExchange;

import java.text.DecimalFormat;

public class CurrencyExchangeDto {
    private final ObjectMapper mapper = new ObjectMapper();

    public String exchangeCurrency(String baseCode, String targetCode, double amount) {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        CurrencyExchange currencyExchange = new CurrencyExchange(baseCode, targetCode);
        double rate = currencyExchange.translate();
        JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();
        Currency baseCurrency = jdbcCurrencyDao.findByCode(baseCode).orElseThrow(
                () -> new NotFoundException("element not found")
        );
        Currency targetCurrency = jdbcCurrencyDao.findByCode(targetCode).orElseThrow(
                () -> new NotFoundException("element not found")
        );
        HelperCurrencyExchange helper = new HelperCurrencyExchange(baseCurrency, targetCurrency,
                rate, amount);
        try {
            return mapper.writeValueAsString(helper);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static class HelperCurrencyExchange {
        private Currency baseCurrency;
        private Currency targetCurrency;
        private String rate;
        private String amount;
        private String convertedAmount;

        public HelperCurrencyExchange(Currency baseCurrency, Currency targetCurrency,
                                      double rate, double amount) {
            this.baseCurrency = baseCurrency;
            this.targetCurrency = targetCurrency;
            DecimalFormat decFormat = new DecimalFormat("#.##");
            this.rate = decFormat.format(rate).replace(',', '.');
            this.amount = decFormat.format(amount).replace(',', '.');
            this.convertedAmount = decFormat.format(amount * rate).replace(',', '.');
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
    }
}
