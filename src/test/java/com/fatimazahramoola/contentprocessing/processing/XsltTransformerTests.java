package com.fatimazahramoola.contentprocessing.processing;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class XsltTransformerTests {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final XsltTransformer xsltTransformer = new XsltTransformer();

	@Test
	void transformsLegalJudgmentXmlIntoNormalizedJson() throws Exception {
		String json = xsltTransformer.transform("""
				<judgment xmlns="urn:lex:content:1">
					<header>
						<content_id>za-gp-2024-001</content_id>
						<title>Moola v Minister of Documents</title>
						<court>High Court of South Africa</court>
						<jurisdiction>ZA-GP</jurisdiction>
						<decision_date>2024-05-17</decision_date>
						<citations>
							<citation type="neutral">[2024] ZAGP 1</citation>
							<citation type="reporter">2024 (1) SA 100 (GP)</citation>
						</citations>
						<parties>
							<party role="applicant">Fatima Moola</party>
							<party role="respondent">Minister of Documents</party>
						</parties>
					</header>
					<body>
						<section type="facts">
							<p id="p1">This is the first paragraph.</p>
						</section>
						<section type="reasons">
							<p id="p2">This is the second paragraph.</p>
						</section>
					</body>
				</judgment>
				""");

		JsonNode document = objectMapper.readTree(json);

		assertThat(document.get("content_id").asText()).isEqualTo("za-gp-2024-001");
		assertThat(document.get("title").asText()).isEqualTo("Moola v Minister of Documents");
		assertThat(document.get("court").asText()).isEqualTo("High Court of South Africa");
		assertThat(document.get("jurisdiction").asText()).isEqualTo("ZA-GP");
		assertThat(document.get("decision_date").asText()).isEqualTo("2024-05-17");
		assertThat(document.get("citations").get(0).get("type").asText()).isEqualTo("neutral");
		assertThat(document.get("citations").get(0).get("value").asText()).isEqualTo("[2024] ZAGP 1");
		assertThat(document.get("citations").get(1).get("type").asText()).isEqualTo("reporter");
		assertThat(document.get("citations").get(1).get("value").asText()).isEqualTo("2024 (1) SA 100 (GP)");
		assertThat(document.get("parties").get(0).get("role").asText()).isEqualTo("applicant");
		assertThat(document.get("parties").get(0).get("name").asText()).isEqualTo("Fatima Moola");
		assertThat(document.get("parties").get(1).get("role").asText()).isEqualTo("respondent");
		assertThat(document.get("parties").get(1).get("name").asText()).isEqualTo("Minister of Documents");
		assertThat(document.get("paragraphs").get(0).get("id").asText()).isEqualTo("p1");
		assertThat(document.get("paragraphs").get(0).get("section").asText()).isEqualTo("facts");
		assertThat(document.get("paragraphs").get(0).get("text").asText()).isEqualTo("This is the first paragraph.");
		assertThat(document.get("paragraphs").get(1).get("id").asText()).isEqualTo("p2");
		assertThat(document.get("paragraphs").get(1).get("section").asText()).isEqualTo("reasons");
		assertThat(document.get("paragraphs").get(1).get("text").asText()).isEqualTo("This is the second paragraph.");
		assertThat(document.get("full_text").asText())
				.isEqualTo("This is the first paragraph. This is the second paragraph.");
	}

}
