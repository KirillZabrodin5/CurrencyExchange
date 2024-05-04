import dao.JdbcCurrencyDao;
import dao.JdbcExchangeRateDao;
import dto.CurrencyDto;
import dto.ExchangeRateDto;
import model.Currency;
import model.ExchangeRate;
import utils.ValidatorCode;

public class RunnerApplication {
    public static void main(String[] args) {
        ExchangeRateDto dto = new ExchangeRateDto();
        System.out.println(dto.saveExchangeRate( "RUB", "USD", 10.1546789));


    }
}
