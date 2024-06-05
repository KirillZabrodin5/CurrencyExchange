package utils;

import Exceptions.InvalidParameterException;
import dto.CurrencyDto;

import java.util.Currency;
import java.util.Set;

public class ValidationUtil {
    public static void validateCurrencyDto(CurrencyDto currencyDto) {

    }

    public static void validateCurrencyCode(String code) {
        if (code == null) {
            throw new InvalidParameterException("There is no currency code in the request");
        }

        if (code.length() != 3) {
            throw new InvalidParameterException("The code length must be 3 characters");
        }

        Set<Currency> currencies = Currency.getAvailableCurrencies();
        Currency currency = Currency.getInstance(code);
        if (!currencies.contains(currency)) {
            throw new InvalidParameterException("There is no currency with code " + code);
        }
    }
}
