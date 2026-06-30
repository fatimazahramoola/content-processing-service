package com.fatimazahramoola.contentprocessing.processing;

import static org.assertj.core.api.Assertions.assertThat;

import com.fatimazahramoola.contentprocessing.api.ProcessingStatus;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingRequest;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingResponse;
import org.junit.jupiter.api.Test;

class XmlProcessingServiceTests {

	private final XmlProcessingService processingService = new XmlProcessingService(
			new XmlValidator(),
			new XsltTransformer());

	@Test
	void returnsAcceptedWhenXmlIsWellFormed() {
		XmlProcessingResponse response = processingService.process(
				new XmlProcessingRequest("example.xml", """
						<judgment xmlns="urn:lex:content:1">
							<header>
								<content_id>example</content_id>
								<title>Example</title>
								<court>Example Court</court>
								<jurisdiction>ZA</jurisdiction>
								<decision_date>2024-05-17</decision_date>
								<citations/>
								<parties/>
							</header>
							<body>
								<section type="facts">
									<p id="p1">Example paragraph.</p>
								</section>
							</body>
						</judgment>
						"""));

		assertThat(response.documentName()).isEqualTo("example.xml");
		assertThat(response.status()).isEqualTo(ProcessingStatus.ACCEPTED);
		assertThat(response.diagnostic()).isNull();
		assertThat(response.normalizedJson()).contains("\"content_id\"");
	}

	@Test
	void returnsRejectedWithDiagnosticWhenXmlIsMalformed() {
		XmlProcessingResponse response = processingService.process(
				new XmlProcessingRequest("example.xml", "<document>"));

		assertThat(response.documentName()).isEqualTo("example.xml");
		assertThat(response.status()).isEqualTo(ProcessingStatus.REJECTED);
		assertThat(response.diagnostic()).isEqualTo("XML is not well-formed.");
		assertThat(response.normalizedJson()).isNull();
	}

}
