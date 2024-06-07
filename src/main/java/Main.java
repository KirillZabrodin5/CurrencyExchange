import dto.CurrencyDto;
import utils.ConverterUtil;

public class Main {
    public static void main(String[] args) {
        CurrencyDto currencyDto = new CurrencyDto("USD", "US Dollar", "$");
        ConverterUtil converterUtil = new ConverterUtil();
        System.out.println(converterUtil.dtoToCurrency(currencyDto));
    }
}
