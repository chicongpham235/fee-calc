package com.drools.fee_calc.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeeCalc {
    @NotNull
    private Transaction transaction;

    @NotNull
    private Customer customer;
}
