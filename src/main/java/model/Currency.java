package model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

@Data
@JsonAutoDetect
public class Currency{
    private Integer id;
    private String code;
    private String name;
    private String sign;

    public Currency() {}

    public Currency(String code, String fullName, String sign) {
        this.code = code;
        this.name = fullName;
        this.sign = sign;
    }

    public Currency(Integer id, String code, String fullName, String sign) {
        this.id = id;
        this.code = code;
        this.name = fullName;
        this.sign = sign;
    }

}
