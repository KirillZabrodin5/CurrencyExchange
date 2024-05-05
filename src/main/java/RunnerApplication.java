import dao.JdbcCurrencyDao;
import dto.CurrencyExchangeDto;
import dto.ExchangeRateDto;
import model.Currency;

public class RunnerApplication {
    public static void main(String[] args) {
        ExchangeRateDto exchangeRateDto = new ExchangeRateDto();
        System.out.println(exchangeRateDto.getJsonAllExchangeRate());
    }
}
