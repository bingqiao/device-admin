package com.bqiao;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SpringApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(SpringApplication.class)
				.application()
				.run(args);
	}

}
