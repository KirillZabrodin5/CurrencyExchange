import com.fasterxml.jackson.core.JsonToken;
import dao.JdbcCurrencyDao;
import dao.JdbcExchangeRateDao;
import dto.CurrencyExchangeDto;
import dto.ExchangeRateDto;
import model.Currency;
import model.ExchangeRate;

public class RunnerApplication {
    public static void main(String[] args) {
        JdbcExchangeRateDao exchangeRateDao = new JdbcExchangeRateDao();
        JdbcCurrencyDao currencyDao = new JdbcCurrencyDao();
        Currency currency1 = currencyDao.findByCode("US").get();
        Currency currency2 = currencyDao.findByCode("RUB").get();
        System.out.println(exchangeRateDao.save(new ExchangeRate(currency1, currency2, 63.75)).get());
    }
}
