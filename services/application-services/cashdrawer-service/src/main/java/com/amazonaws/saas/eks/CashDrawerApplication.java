package com.amazonaws.saas.eks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CashDrawerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CashDrawerApplication.class, args);
	}
}