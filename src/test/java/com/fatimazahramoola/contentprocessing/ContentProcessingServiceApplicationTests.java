package com.fatimazahramoola.contentprocessing;

import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

class ContentProcessingServiceApplicationTests {

	@Test
	void contextLoads() {
		try (ConfigurableApplicationContext context = new SpringApplicationBuilder(
				ContentProcessingServiceApplication.class)
				.web(WebApplicationType.NONE)
				.run()) {
		}
	}

}
