package com.drools.fee_calc.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drools.fee_calc.model.FeeCalc;
import com.drools.fee_calc.service.CacheService;
import com.drools.fee_calc.service.FeeCalcService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/fee")
@CrossOrigin(origins = "*")
public class FeeCalcController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FeeCalcController.class);

    @Autowired
    FeeCalcService feeCalcService;

    @Autowired
    CacheService cacheService;

    @PostMapping("/fee-calc")
    public ResponseEntity<Double> calculateFee(@RequestBody @Valid FeeCalc paylod) {
        Double fee = feeCalcService.calculateFee(
                paylod.getTransaction(), paylod.getCustomer());
        return ResponseEntity.ok(fee);

    }

    @PostMapping("/khai-bao-tham-so")
    public ResponseEntity<Boolean> khaiBaoThamSo(
            @RequestBody @Valid Map<String, String> payload) {
        String key = payload.get("code");
        boolean result = cacheService.put(key, payload);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/get-khai-bao-tham-so")
    public Object test(@RequestBody Map<String, String> payload) {
        // TODO: process POST request
        logger.info("Get key: {}", payload.get("key"));
        return ResponseEntity.ok(cacheService.get(payload.get("key")));
    }

}
