package com.fatimazahramoola.contentprocessing.processing;

import static org.assertj.core.api.Assertions.assertThat;

import com.fatimazahramoola.contentprocessing.api.ProcessingStatus;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingRequest;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingResponse;
import org.junit.jupiter.api.Test;

class XmlProcessingServiceTests {

	private final XmlProcessingService processingService = new XmlProcessingService(new XmlValidator());

	@Test
	void returnsAcceptedWhenXmlIsWellFormed() {
		XmlProcessingResponse response = processingService.process(
				new XmlProcessingRequest("example.xml", "<document />"));

		assertThat(response.documentName()).isEqualTo("example.xml");
		assertThat(response.status()).isEqualTo(ProcessingStatus.ACCEPTED);
		assertThat(response.diagnostic()).isNull();
	}

	@Test
	void returnsRejectedWithDiagnosticWhenXmlIsMalformed() {
		XmlProcessingResponse response = processingService.process(
				new XmlProcessingRequest("example.xml", "<document>"));

		assertThat(response.documentName()).isEqualTo("example.xml");
		assertThat(response.status()).isEqualTo(ProcessingStatus.REJECTED);
		assertThat(response.diagnostic()).isEqualTo("XML is not well-formed.");
	}

}
