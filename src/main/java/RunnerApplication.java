import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.JdbcCurrencyDao;
import model.Currency;

import java.util.List;

public class RunnerApplication {
    public static void main(String[] args) throws JsonProcessingException {
        JdbcCurrencyDao jdbcCurrencyDao = new JdbcCurrencyDao();
        List<Currency> currencies = jdbcCurrencyDao.findAll();

        ObjectMapper objectMapper = new ObjectMapper();
        String answer = objectMapper.writeValueAsString(currencies);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        System.out.println(answer);
    }
}
