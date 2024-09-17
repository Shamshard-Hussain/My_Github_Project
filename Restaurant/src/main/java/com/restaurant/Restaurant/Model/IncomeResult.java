package com.restaurant.Restaurant.Model;

import java.math.BigDecimal;

// Helper class to hold aggregation results
public class IncomeResult {
    private BigDecimal totalIncome;

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

}
