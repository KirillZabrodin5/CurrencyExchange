package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class Currency {
    private Long id;
    private String code;
    private String fullName;
    private String sign;

    public Currency(String code) {
        this.code = code;
    }
}
