import dto.CurrencyExchangeDto;

public class RunnerApplication {
    public static void main(String[] args) {
        CurrencyExchangeDto currencyExchangeDto = new CurrencyExchangeDto();
        System.out.println(currencyExchangeDto.exchangeCurrency("RUB", "EUR", 100));
    }
}
