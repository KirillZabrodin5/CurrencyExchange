package dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.RequestDb;
import model.Currency;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Класс для преобразования SQL запроса в Json
 * */
public class CurrencyDTO {
    public void getJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            File file = new File("resources/jsonCur.json");

            List<Currency> currencies = RequestDb.getAllCurrencies();

            mapper.writeValue(file, currencies);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
