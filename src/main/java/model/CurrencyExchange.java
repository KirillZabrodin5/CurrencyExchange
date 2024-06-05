package model;

import java.text.DecimalFormat;

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

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(Currency targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(double convertedAmount) {
        this.convertedAmount = convertedAmount;
    }
}