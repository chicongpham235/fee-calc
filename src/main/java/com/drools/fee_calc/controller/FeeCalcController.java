package com.drools.fee_calc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drools.fee_calc.enums.ChannelTransaction;
import com.drools.fee_calc.enums.TierCustomer;
import com.drools.fee_calc.enums.TypeCustomer;
import com.drools.fee_calc.enums.TypeTransaction;
import com.drools.fee_calc.model.FeeCalc;
import com.drools.fee_calc.service.FeeCalcService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/fee")
@CrossOrigin(origins = "*")
public class FeeCalcController {

    @Autowired
    FeeCalcService feeCalcService;

    @PostMapping("/fee-calc")
    public ResponseEntity<Double> calculateFee(@RequestBody @Valid FeeCalc paylod) {
        Double fee = feeCalcService.calculateFee(
                paylod.getTransaction(), paylod.getCustomer());
        return ResponseEntity.ok(fee);

    }
}
