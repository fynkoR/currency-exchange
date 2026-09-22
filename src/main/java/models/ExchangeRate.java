package models;

import java.math.BigDecimal;

public class ExchangeRate {
    private int id;
    private final int baseCurrencyId;
    private final int targetCurrencyId;
    private final BigDecimal rate;

    public ExchangeRate(int baseCurrencyId, int targetCurrencyId, BigDecimal rate){
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }
}
