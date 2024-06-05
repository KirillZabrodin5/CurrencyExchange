package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@ToString()
public class Currency {
    private Long id = null;
    private String code = null;
    private String name = null;
    private String sign = null;

    public Currency(Long id) {
        this.id = id;
    }
}
