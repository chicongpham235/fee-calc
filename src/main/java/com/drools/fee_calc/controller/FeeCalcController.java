package com.drools.fee_calc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drools.fee_calc.enums.ChannelTransaction;
import com.drools.fee_calc.enums.TierCustomer;
import com.drools.fee_calc.enums.TypeCustomer;
import com.drools.fee_calc.enums.TypeTransaction;
import com.drools.fee_calc.service.FeeCalcService;

@RestController
@RequestMapping("/fee")
@CrossOrigin(origins = "*")
public class FeeCalcController {

    @Autowired
    FeeCalcService feeCalcService;

    @PostMapping("/fee-calc")
    public Double calculateFee() {
        return feeCalcService.calculateFee(
                100000000.00,
                TypeTransaction.TRANSFER
                        .toString(),
                ChannelTransaction.ONLINE.toString(),
                "Nguyen Van A",
                TierCustomer.DIAMOND.toString(),
                TypeCustomer.PRIVATE.toString());

    }
}
