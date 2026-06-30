package com.fatimazahramoola.contentprocessing.processing;

import static org.assertj.core.api.Assertions.assertThat;

import com.fatimazahramoola.contentprocessing.api.ProcessingStatus;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingRequest;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingResponse;
import com.fatimazahramoola.contentprocessing.publishing.InMemoryArtifactStore;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class XmlProcessingServiceTests {

	private final InMemoryArtifactStore artifactStore = new InMemoryArtifactStore();
	private final XmlProcessingService processingService = new XmlProcessingService(
			new XmlValidator(),
			new XsltTransformer(),
			new XmlMetadataExtractor(),
			artifactStore,
			Clock.fixed(Instant.parse("2024-05-17T10:15:30Z"), ZoneOffset.UTC));

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
		assertThat(artifactStore.findByContentId("example"))
				.hasValueSatisfying(artifact -> {
					assertThat(artifact.contentId()).isEqualTo("example");
					assertThat(artifact.documentName()).isEqualTo("example.xml");
					assertThat(artifact.normalizedJson()).isEqualTo(response.normalizedJson());
					assertThat(artifact.publishedAt()).isEqualTo(Instant.parse("2024-05-17T10:15:30Z"));
				});
	}

	@Test
	void returnsRejectedWithDiagnosticWhenXmlIsMalformed() {
		XmlProcessingResponse response = processingService.process(
				new XmlProcessingRequest("example.xml", "<document>"));

		assertThat(response.documentName()).isEqualTo("example.xml");
		assertThat(response.status()).isEqualTo(ProcessingStatus.REJECTED);
		assertThat(response.diagnostic()).isEqualTo("XML is not well-formed.");
		assertThat(response.normalizedJson()).isNull();
		assertThat(artifactStore.findAll()).isEmpty();
	}

}
