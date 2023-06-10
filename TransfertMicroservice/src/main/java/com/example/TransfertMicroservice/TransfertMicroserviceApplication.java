package com.example.TransfertMicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class TransfertMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransfertMicroserviceApplication.class, args);
	}

}
