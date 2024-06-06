package utils;

import dto.CurrencyDto;
import dto.ExchangeRateDto;
import entities.Currency;
import entities.ExchangeRate;
import org.modelmapper.ModelMapper;

public class ConverterCurrencyUtil {
    private static final ModelMapper modelMapper = new ModelMapper();

    public ConverterCurrencyUtil() {
        modelMapper.typeMap(CurrencyDto.class, Currency.class)
                .addMapping(CurrencyDto::getName, Currency::setFullName);
    }

    public Currency dtoToEntity(CurrencyDto dto) {
        return new Currency(dto.getId(), dto.getCode(), dto.getName(), dto.getSign());
        //modelMapper.map(dto, Currency.class);
    }

    public ExchangeRate dtoToExchangeRate(ExchangeRateDto dto) {
        return new ExchangeRate(null, dto.getBaseCurrency(), dto.getTargetCurrency(), dto.getRate());
    }
}
