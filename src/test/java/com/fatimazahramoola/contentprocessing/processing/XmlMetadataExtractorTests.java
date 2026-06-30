package com.fatimazahramoola.contentprocessing.processing;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class XmlMetadataExtractorTests {

	private final XmlMetadataExtractor xmlMetadataExtractor = new XmlMetadataExtractor();

	@Test
	void extractsContentIdFromValidatedXml() {
		String contentId = xmlMetadataExtractor.contentId("""
				<judgment xmlns="urn:lex:content:1">
					<header>
						<content_id>za-gp-2024-001</content_id>
					</header>
					<body/>
				</judgment>
				""");

		assertThat(contentId).isEqualTo("za-gp-2024-001");
	}

}
