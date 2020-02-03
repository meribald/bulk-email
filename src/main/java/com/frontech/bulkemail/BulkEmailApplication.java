package com.frontech.bulkemail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class BulkEmailApplication {

	public static void main(String[] args) {
		SpringApplication.run(BulkEmailApplication.class, args);
	}

}
