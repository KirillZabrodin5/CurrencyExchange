import dao.RequestDb;
import dto.CurrencyDTO;

public class RunnerApplication {
    public static void main(String[] args)  {
        CurrencyDTO currencyDTO = new CurrencyDTO();
        currencyDTO.getJson();

        RequestDb dbUtil = new RequestDb();
        dbUtil.updateExchangeRates("USD", "RUB", 34);
    }
}
