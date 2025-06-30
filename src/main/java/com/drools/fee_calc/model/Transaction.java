package com.drools.fee_calc.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction {
    private Double amount;
    private String type;
    private String channel;
    private Double fee = 0.0;
}
