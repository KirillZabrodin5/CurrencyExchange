package dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.ReceivedRate;
import dao.RequestDbUtil;
import model.Currency;
import model.ExchangeRates;

import java.io.File;
import java.io.IOException;

/**
 * Класс для преобразования SQL запроса в Json
 * */
public class CurrencyDTO {
    public void getJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            File file = new File("request.json");
            RequestDbUtil dbUtil = new RequestDbUtil();

            Currency baseCurrency = RequestDbUtil.getCurrencyByCode("USD");
            Currency targetCurrency = RequestDbUtil.getCurrencyByCode("RUB");

//            Map<String, Object> json = new LinkedHashMap<>();
//            json.put("baseCurrency", baseCurrency);
//            json.put("targetCurrency", targetCurrency);
//            json.put("rate", 1.45);
//            json.put("amount", 10.00);
//            json.put("convertedAmount", 14.50);

            mapper.writeValue(file, dbUtil
                    .getExchangeRateByCode("USD", "RUB"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
