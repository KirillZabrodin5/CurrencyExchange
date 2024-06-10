package dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Setter
@Getter
@NoArgsConstructor
public class CurrencyExchangeDto {
    private CurrencyDto baseCurrency;
    private CurrencyDto targetCurrency;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;

    public CurrencyExchangeDto(CurrencyDto baseCurrency, CurrencyDto targetCurrency,
                               BigDecimal rate, BigDecimal amount) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate.setScale(2, RoundingMode.HALF_UP);
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.convertedAmount = new BigDecimal(String.valueOf(rate.multiply(this.amount)))
                .setScale(2, RoundingMode.HALF_UP);
    }
}