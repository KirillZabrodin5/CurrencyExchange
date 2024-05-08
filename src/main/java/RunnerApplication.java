import dto.CurrencyDto;
import dto.CurrencyExchangeDto;
import model.CurrencyExchange;

public class RunnerApplication {
    public static void main(String[] args) {
        CurrencyDto data = new CurrencyDto();
        String answer = data.getAllJson();
        System.out.println(answer);
    }
}
