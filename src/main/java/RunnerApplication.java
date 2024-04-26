import dao.RequestDbUtil;
import dto.CurrencyDTO;

public class RunnerApplication {
    public static void main(String[] args)  {
        CurrencyDTO currencyDTO = new CurrencyDTO();
        currencyDTO.getJson();

        RequestDbUtil dbUtil = new RequestDbUtil();
        dbUtil.updateExchangeRates("USD", "RUB", 34);
    }
}
