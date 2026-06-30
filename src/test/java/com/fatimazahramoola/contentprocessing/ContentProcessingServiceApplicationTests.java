package com.fatimazahramoola.contentprocessing;

import com.fatimazahramoola.contentprocessing.api.ProcessingService;
import com.fatimazahramoola.contentprocessing.api.ProcessingStatus;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

class ContentProcessingServiceApplicationTests {

	@Test
	void contextLoads() {
		try (ConfigurableApplicationContext context = new SpringApplicationBuilder(
				ContentProcessingServiceApplication.class,
				ProcessingServiceTestConfiguration.class)
				.web(WebApplicationType.NONE)
				.run()) {
		}
	}

	@TestConfiguration
	static class ProcessingServiceTestConfiguration {

		@Bean
		ProcessingService processingService() {
			return request -> new XmlProcessingResponse(request.documentName(), ProcessingStatus.ACCEPTED);
		}

	}

}
