package dto;

import entities.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateDto {
    Long id = 0L;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private double rate;

    public ExchangeRateDto(Currency baseCurrency, Currency targetCurrency) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
    }

    public ExchangeRateDto(Currency baseCurrency, Currency targetCurrency, double rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }
}
