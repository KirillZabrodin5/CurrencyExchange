package dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.RequestDbUtil;

import java.io.File;
import java.io.IOException;

public class JsonMapper {
    public void method() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            File file = new File("test.json");

            mapper.writeValue(file, RequestDbUtil.findCurrencyByCode("USD"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
