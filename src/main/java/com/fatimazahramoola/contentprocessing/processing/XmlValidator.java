package com.fatimazahramoola.contentprocessing.processing;

import java.io.StringReader;
import java.util.Optional;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.stereotype.Component;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@Component
public class XmlValidator {

	public Optional<String> validate(String xml) {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
			documentBuilderFactory.setExpandEntityReferences(false);
			var documentBuilder = documentBuilderFactory.newDocumentBuilder();
			documentBuilder.setErrorHandler(new ErrorHandler() {

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

			});
			documentBuilder.parse(new InputSource(new StringReader(xml)));
			return Optional.empty();
		}
		catch (ParserConfigurationException exception) {
			return Optional.of("XML validation could not be configured.");
		}
		catch (SAXException exception) {
			return Optional.of("XML is not well-formed.");
		}
		catch (Exception exception) {
			return Optional.of("XML could not be read.");
		}
	}

}
