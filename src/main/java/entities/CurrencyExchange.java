package entities;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Setter
@Getter
public class CurrencyExchange {
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;

    public CurrencyExchange(Currency baseCurrency, Currency targetCurrency,
                            double rate, double amount) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = new BigDecimal(rate).setScale(2, RoundingMode.HALF_UP);
        this.amount = new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP);
        this.convertedAmount = new BigDecimal(amount * rate).setScale(2, RoundingMode.HALF_UP);
    }
}