package com.example.DeplacementMicroservice;

import com.DTOLibrary.Communication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DeplacementMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeplacementMicroserviceApplication.class, args);
	}

}
