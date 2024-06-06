package utils;

import Exceptions.InvalidParameterException;
import dto.CurrencyDto;
import dto.CurrencyExchangeDto;

import java.util.Currency;
import java.util.Set;

public class ValidationUtil {
    public static void validateCurrencyDto(CurrencyDto currencyDto) {
        validateCurrencyCode(currencyDto.getCode());

        if (currencyDto.getName() == null) {
            throw new InvalidParameterException("Name is null");
        }

        if (currencyDto.getCode() == null) {
            throw new InvalidParameterException("Code is null");
        }
    }

    public static void validateCurrencyExchangeDto(CurrencyExchangeDto currencyExchangeDto) {
        validateCurrencyCode(currencyExchangeDto.getBaseCurrencyCode());
        validateCurrencyCode(currencyExchangeDto.getTargetCurrencyCode());
        if (currencyExchangeDto.getAmount() <= 0) {
            throw new InvalidParameterException("Amount must be greater than 0");
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
        Currency currency = Currency.getInstance(code);
        if (!currencies.contains(currency)) {
            throw new InvalidParameterException("There is no currency with code " + code);
        }
    }
}
