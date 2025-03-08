package com.AiRag.IndianContiutionRag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "RestApi")
public class IndianContiutionRagApplication {

	public static void main(String[] args) {
		SpringApplication.run(IndianContiutionRagApplication.class, args);
		System.out.println("here.....");
		

	}

}
