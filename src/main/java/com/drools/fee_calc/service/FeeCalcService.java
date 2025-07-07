package com.drools.fee_calc.service;

import com.drools.fee_calc.model.Customer;
import com.drools.fee_calc.model.Transaction;

public interface FeeCalcService {
    Double calculateFee(Transaction transaction, Customer customer);
}
