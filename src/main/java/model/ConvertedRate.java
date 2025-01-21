package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConvertedRate {
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
    private Double amount;
    private Double convertedAmount;

    public ConvertedRate(Currency baseCurrency, Currency targetCurrency, BigDecimal rate, Double amount) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = rate.multiply(BigDecimal.valueOf(amount)).doubleValue();
    }

}
