package com.universidade.sighoras;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.universidade.sighoras")
public class HorasUfsjApplication {

	public static void main(String[] args) {
		SpringApplication.run(HorasUfsjApplication.class, args);
	}

}
