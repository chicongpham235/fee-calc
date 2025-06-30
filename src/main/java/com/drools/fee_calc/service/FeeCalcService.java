package com.drools.fee_calc.service;

public interface FeeCalcService {
    Double calculateFee(Double amount, String type, String channel, String customerName, String customerTier,
            String customerType);
}
