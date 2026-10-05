package models;

public class Currency {
    private Long id;
    private String code;
    private String name;
    private String sign;

    public Currency(){}

    public Currency(String code, String name, String sign){
        this.code = code;
        this.name = name;
        this.sign = sign;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSign() {
        return sign;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setCode(String code){
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
