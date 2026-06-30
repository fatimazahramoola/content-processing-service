package com.fatimazahramoola.contentprocessing.processing;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class XmlValidatorTests {

	private final XmlValidator xmlValidator = new XmlValidator();

	@Test
	void returnsNoDiagnosticForSchemaCompliantXml() {
		assertThat(xmlValidator.validate("""
				<judgment xmlns="urn:lex:content:1">
					<header>
						<content_id>example</content_id>
						<title>Example</title>
						<court>Example Court</court>
						<jurisdiction>ZA</jurisdiction>
						<decision_date>2024-05-17</decision_date>
						<citations>
							<citation type="neutral">[2024] ZAGP 1</citation>
						</citations>
						<parties>
							<party role="applicant">Fatima Moola</party>
						</parties>
					</header>
					<body>
						<section type="facts">
							<p id="p1">Example paragraph.</p>
						</section>
					</body>
				</judgment>
				""")).isEmpty();
	}

	@Test
	void returnsDiagnosticForXmlThatViolatesSchema() {
		assertThat(xmlValidator.validate("""
				<judgment xmlns="urn:lex:content:1">
					<header>
						<content_id>example</content_id>
						<title>Example</title>
						<court>Example Court</court>
						<jurisdiction>ZA</jurisdiction>
						<decision_date>not-a-date</decision_date>
						<citations/>
						<parties/>
					</header>
					<body>
						<section type="facts">
							<p id="p1">Example paragraph.</p>
						</section>
					</body>
				</judgment>
				""")).contains("XML does not conform to the judgment schema.");
	}

	@Test
	void returnsDiagnosticForMalformedXml() {
		assertThat(xmlValidator.validate("<document><title>Example</document>"))
				.contains("XML does not conform to the judgment schema.");
	}

}
