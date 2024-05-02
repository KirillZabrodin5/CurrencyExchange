import dao.JdbcCurrencyDao;
import model.Currency;

import java.util.Optional;

public class RunnerApplication {
    public static void main(String[] args)  {
        JdbcCurrencyDao dao = new JdbcCurrencyDao();
        Currency currency = new Currency("USD", "dollar", "$");
        dao.save(currency);
    }
}
