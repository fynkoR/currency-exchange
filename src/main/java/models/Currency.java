package models;

public class Currency {
    private Long id;
    private final String code;
    private final String fullName;
    private final String sign;

    public Currency(String code, String fullName, String sign){
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getFullName() {
        return fullName;
    }

    public String getSign() {
        return sign;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
