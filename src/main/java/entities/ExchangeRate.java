package entities;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate {
    private Long id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private double rate;

    public ExchangeRate(Currency baseCurrency, Currency targetCurrency, double rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }
}
