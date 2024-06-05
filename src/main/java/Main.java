import dao.JdbcCurrencyDao;
import dto.CurrencyDto;
import entities.Currency;

public class Main {
    public static void main(String[] args) {
        CurrencyDto currency = new CurrencyDto("USE", "Dollar", "$");
        System.out.println(new JdbcCurrencyDao().findByCode(currency).orElseThrow());
    }
}
