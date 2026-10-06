package dto;

import model.Currency;

import java.math.BigDecimal;

public class ExchangeRateDto {
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;

    public ExchangeRateDto(Currency baseCurrency, Currency targetCurrency, BigDecimal rate,
                           BigDecimal amount, BigDecimal convertedAmount) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }
}
