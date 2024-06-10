package utils;

import exception.InvalidParameterException;
import exception.NotFoundException;
import dto.CurrencyDto;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Set;

public final class ValidationUtil {
    public static void validateCurrencyDto(CurrencyDto currencyDto) {
        validateCurrencyCode(currencyDto.getCode());

        if (currencyDto.getName() == null) {
            throw new InvalidParameterException("Name is null");
        }

        if (currencyDto.getSign() == null) {
            throw new InvalidParameterException("Sign is null");
        }
    }

    public static void validateCurrencyCode(String code) {
        if (code == null) {
            throw new InvalidParameterException("There is no currency code in the request");
        }

        if (code.length() != 3) {
            throw new InvalidParameterException("The code length must be 3 characters");
        }

        Set<Currency> currencies = Currency.getAvailableCurrencies();
        List<String> codes = new ArrayList<>(currencies.size());
        for(Currency currency : currencies) {
            codes.add(currency.getCurrencyCode());
        }

        if (!codes.contains(code)) {
            throw new NotFoundException("There is no currency with code " + code);
        }
    }
}
