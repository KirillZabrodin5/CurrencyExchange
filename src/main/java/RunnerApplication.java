import dao.JdbcCurrencyDao;
import model.Currency;

public class RunnerApplication {
    public static void main(String[] args)  {
        JdbcCurrencyDao dao = new JdbcCurrencyDao();
        Currency currency = dao.getCurrencyByCode("USD");
        System.out.println(currency);


    }
}
