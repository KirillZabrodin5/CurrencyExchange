package dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.RequestDbUtil;
import model.Currency;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Класс для преобразования SQL запроса в Json
 * */
public class CurrencyDTO {
    public void getJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            File file = new File("request.json");
            Currency baseCurrency = RequestDbUtil.findCurrencyByCode("USD");
            Currency targetCurrency = RequestDbUtil.findCurrencyByCode("RUB");

//            Map<String, Object> json = new LinkedHashMap<>();
//            json.put("baseCurrency", baseCurrency);
//            json.put("targetCurrency", targetCurrency);
//            json.put("rate", 1.45);
//            json.put("amount", 10.00);
//            json.put("convertedAmount", 14.50);

            mapper.writeValue(file, baseCurrency);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
