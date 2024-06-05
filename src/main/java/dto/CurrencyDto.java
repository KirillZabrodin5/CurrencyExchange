package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CurrencyDto {
    private String code = null;
    private String name = null;
    private String sign = null;

    public CurrencyDto(String code) {
        this.code = code;
    }
}
