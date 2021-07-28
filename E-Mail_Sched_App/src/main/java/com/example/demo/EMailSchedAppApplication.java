package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class EMailSchedAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(EMailSchedAppApplication.class, args);
	}

}
