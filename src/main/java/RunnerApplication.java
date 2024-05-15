import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.JdbcCurrencyDao;
import model.Currency;

import java.util.List;

public class RunnerApplication {
    public static void main(String[] args) throws JsonProcessingException {
        String str = "jfdsjf";

        System.out.println(str == null);
    }
}
