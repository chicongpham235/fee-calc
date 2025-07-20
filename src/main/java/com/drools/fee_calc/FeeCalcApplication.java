package com.drools.fee_calc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FeeCalcApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeeCalcApplication.class, args);
	}

}
