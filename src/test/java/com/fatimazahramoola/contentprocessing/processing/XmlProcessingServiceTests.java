package com.fatimazahramoola.contentprocessing.processing;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import com.fatimazahramoola.contentprocessing.api.ProcessingStatus;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingRequest;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingResponse;
import com.fatimazahramoola.contentprocessing.publishing.InMemoryArtifactStore;

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
		assertThat(response.diagnostic()).isEqualTo("XML does not conform to the judgment schema.");
		assertThat(response.normalizedJson()).isNull();
		assertThat(artifactStore.findAll()).isEmpty();
	}

	@Test
	void repeatedSubmissionsWithSameContentIdDoNotOverwritePublishedArtifact() {
		XmlProcessingResponse firstResponse = processingService.process(
				new XmlProcessingRequest("first.xml", """
						<judgment xmlns="urn:lex:content:1">
							<header>
								<content_id>example</content_id>
								<title>First Example</title>
								<court>Example Court</court>
								<jurisdiction>ZA</jurisdiction>
								<decision_date>2024-05-17</decision_date>
								<citations/>
								<parties/>
							</header>
							<body>
								<section type="facts">
									<p id="p1">First paragraph.</p>
								</section>
							</body>
						</judgment>
						"""));

		XmlProcessingResponse secondResponse = processingService.process(
				new XmlProcessingRequest("second.xml", """
						<judgment xmlns="urn:lex:content:1">
							<header>
								<content_id>example</content_id>
								<title>Second Example</title>
								<court>Example Court</court>
								<jurisdiction>ZA</jurisdiction>
								<decision_date>2024-05-18</decision_date>
								<citations/>
								<parties/>
							</header>
							<body>
								<section type="facts">
									<p id="p1">Second paragraph.</p>
								</section>
							</body>
						</judgment>
						"""));

		assertThat(firstResponse.status()).isEqualTo(ProcessingStatus.ACCEPTED);
		assertThat(secondResponse.status()).isEqualTo(ProcessingStatus.ACCEPTED);
		assertThat(secondResponse.normalizedJson()).contains("Second Example");
		assertThat(artifactStore.findAll()).hasSize(1);
		assertThat(artifactStore.findByContentId("example"))
				.hasValueSatisfying(artifact -> {
					assertThat(artifact.documentName()).isEqualTo("first.xml");
					assertThat(artifact.normalizedJson()).isEqualTo(firstResponse.normalizedJson());
					assertThat(artifact.normalizedJson()).contains("First Example");
					assertThat(artifact.normalizedJson()).doesNotContain("Second Example");
				});
	}

}
