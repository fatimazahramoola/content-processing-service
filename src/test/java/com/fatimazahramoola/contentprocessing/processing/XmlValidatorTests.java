package com.fatimazahramoola.contentprocessing.processing;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class XmlValidatorTests {

	private final XmlValidator xmlValidator = new XmlValidator();

	@Test
	void returnsNoDiagnosticForWellFormedXml() {
		assertThat(xmlValidator.validate("<document><title>Example</title></document>")).isEmpty();
	}

	@Test
	void returnsDiagnosticForMalformedXml() {
		assertThat(xmlValidator.validate("<document><title>Example</document>"))
				.contains("XML is not well-formed.");
	}

}
