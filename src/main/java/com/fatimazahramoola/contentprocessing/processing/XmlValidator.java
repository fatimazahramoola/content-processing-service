package com.fatimazahramoola.contentprocessing.processing;

import java.io.StringReader;
import java.util.Optional;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Validates legal judgment XML against the bundled XML Schema.
 * A successful validation is required before transformation and publishing continue.
 */
@Component
public class XmlValidator {

	private static final String SCHEMA_PATH = "xsd/judgment.xsd";

	private final Schema schema;

	public XmlValidator() {
		try {
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schemaFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
			this.schema = schemaFactory.newSchema(new StreamSource(new ClassPathResource(SCHEMA_PATH).getInputStream()));
		}
		catch (Exception exception) {
			throw new IllegalStateException("Could not load XML schema.", exception);
		}
	}

	/**
	 * Validates the XML and returns a diagnostic when the document is malformed or schema-invalid.
	 */
	public Optional<String> validate(String xml) {
		try {
			// Validator instances are not thread-safe, so each request gets its own instance.
			Validator validator = schema.newValidator();
			validator.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			validator.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
			validator.setErrorHandler(new QuietXmlErrorHandler());
			validator.validate(new StreamSource(new StringReader(xml)));
			return Optional.empty();
		}
		catch (SAXException exception) {
			return Optional.of("XML does not conform to the judgment schema.");
		}
		catch (Exception exception) {
			return Optional.of("XML could not be read.");
		}
	}

	private static class QuietXmlErrorHandler implements ErrorHandler {

		@Override
		public void warning(SAXParseException exception) throws SAXException {
			throw exception;
		}

		@Override
		public void error(SAXParseException exception) throws SAXException {
			throw exception;
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			throw exception;
		}

	}

}
