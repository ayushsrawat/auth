package com.ayushrawat.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AuthApplication {

	static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

}