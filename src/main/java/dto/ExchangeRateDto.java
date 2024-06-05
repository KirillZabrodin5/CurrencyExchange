package dto;

import entities.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ExchangeRateDto {
    private Currency baseCurrency;
    private Currency targetCurrency;
    private double rate;

    public ExchangeRateDto(Currency baseCurrency, Currency targetCurrency) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
    }
}
