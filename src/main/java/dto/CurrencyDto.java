package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyDto {
    private Long id = 0L;
    private String code = null;
    private String name = null;
    private String sign = null;

    public CurrencyDto(String code) {
        this.code = code;
    }

    public CurrencyDto(String code, String name, String sign) {
        this.code = code;
        this.name = name;
        this.sign = sign;
    }
}
