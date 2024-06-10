package utils;

import dto.CurrencyDto;
import dto.ExchangeRateDto;
import entity.Currency;
import entity.ExchangeRate;
import org.modelmapper.ModelMapper;

public final class ConverterUtil {
    private static final ModelMapper modelMapper = new ModelMapper();

    public ConverterUtil() {
    }

    public Currency dtoToCurrency(CurrencyDto dto) {
        modelMapper.typeMap(CurrencyDto.class, Currency.class)
                .addMapping(CurrencyDto::getName, Currency::setFullName);

        return modelMapper.map(dto, Currency.class);
    }

    public CurrencyDto currencyToDto(Currency currency) {
        modelMapper.typeMap(Currency.class, CurrencyDto.class)
                .addMapping(Currency::getFullName, CurrencyDto::setName);
        return modelMapper.map(currency, CurrencyDto.class);
    }

    public ExchangeRate dtoToExchangeRate(ExchangeRateDto dto) {
        return modelMapper.map(dto, ExchangeRate.class);
    }

    public ExchangeRateDto exchangeRateToDto(ExchangeRate exchangeRate) {
        return modelMapper.map(exchangeRate, ExchangeRateDto.class);
    }
}
