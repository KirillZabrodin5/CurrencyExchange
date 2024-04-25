import dao.ReceivedRate;
import dto.CurrencyDTO;

public class RunnerApplication {
    public static void main(String[] args)  {
        ReceivedRate getRate = new ReceivedRate("RUB", "USD");
        System.out.println(100000*getRate.translate());
        CurrencyDTO currencyDTO = new CurrencyDTO();
        currencyDTO.getJson();
    }
}
