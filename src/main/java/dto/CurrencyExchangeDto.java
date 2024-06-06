package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CurrencyExchangeDto {
    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private double amount;
}
