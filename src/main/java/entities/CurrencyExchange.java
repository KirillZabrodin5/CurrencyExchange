package entities;

import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

@Setter
@Getter
public class CurrencyExchange {
    private Currency baseCurrency;
    private Currency targetCurrency;
    private double rate;
    private double amount;
    private double convertedAmount;

    public CurrencyExchange(Currency baseCurrency, Currency targetCurrency,
                            double rate, double amount) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        DecimalFormat decFormat = new DecimalFormat("#.##");
        this.rate = Double.parseDouble(decFormat.format(rate).replace(',', '.'));
        this.amount = Double.parseDouble(decFormat.format(amount).replace(',', '.'));
        this.convertedAmount = Double.parseDouble(decFormat.format(amount * rate).replace(',', '.'));
    }

}