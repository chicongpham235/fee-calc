package com.drools.fee_calc.service.impl;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.drools.fee_calc.config.DroolsEngineService;
import com.drools.fee_calc.model.Customer;
import com.drools.fee_calc.model.Transaction;
import com.drools.fee_calc.service.FeeCalcService;

@Service
public class FeeCalcServiceImpl implements FeeCalcService {

    @Autowired
    DroolsEngineService droolsEngineService;

    private KieContainer getKieContainer() {
        return droolsEngineService.getKieContainer();
    }

    @Override
    public Double calculateFee(Double amount, String type, String channel, String customerName, String customerTier,
            String customerType) {
        // Create a transaction and customer object
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setChannel(channel);

        Customer customer = new Customer();
        customer.setName(customerName);
        customer.setTier(customerTier);
        customer.setType(customerType);

        // Create a KieSession and insert the objects
        KieSession kieSession = getKieContainer().newKieSession();
        kieSession.insert(transaction);
        kieSession.insert(customer);

        // Fire all rules
        kieSession.fireAllRules();

        // Dispose the session
        kieSession.dispose();

        return transaction.getFee();
    }
}
