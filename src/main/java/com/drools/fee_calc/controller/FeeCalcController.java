package com.drools.fee_calc.controller;

import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drools.fee_calc.config.DroolsEngineService;
import com.drools.fee_calc.enums.ChannelTransaction;
import com.drools.fee_calc.enums.TierCustomer;
import com.drools.fee_calc.enums.TypeCustomer;
import com.drools.fee_calc.enums.TypeTransaction;
import com.drools.fee_calc.model.Customer;
import com.drools.fee_calc.model.Transaction;

@RestController
@RequestMapping("/fee-service")
@CrossOrigin(origins = "*")
public class FeeCalcController {

    @Autowired
    private DroolsEngineService droolsEngineService;

    @PostMapping("/fee-calc")
    public Object testDrool() {
        Transaction transaction = new Transaction();
        transaction.setAmount(100000000.00);
        transaction.setChannel(ChannelTransaction.ONLINE.toString());
        transaction.setType(TypeTransaction.TRANSFER.toString());

        Customer customer = new Customer();
        customer.setName("PCC");
        customer.setTier(TierCustomer.DIAMOND.toString());
        customer.setType(TypeCustomer.CORPORATE.toString());

        KieSession kieSession = droolsEngineService.getKieContainer().newKieSession();
        kieSession.insert(transaction);
        kieSession.insert(customer);
        kieSession.fireAllRules();
        kieSession.dispose();
        return transaction.getFee();
    }
}
