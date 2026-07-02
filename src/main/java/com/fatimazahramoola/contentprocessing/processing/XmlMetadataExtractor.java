package com.fatimazahramoola.contentprocessing.processing;

import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

/**
 * Reads publishing metadata from schema-valid XML.
 * Keeping this separate from transformation avoids coupling metadata extraction to the JSON output.
 */
@Component
public class XmlMetadataExtractor {

	private static final String CONTENT_NAMESPACE = "urn:lex:content:1";

	/**
	 * Extracts the stable content identifier used as the artifact publishing key.
	 */
	public String contentId(String xml) {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
			var documentBuilder = documentBuilderFactory.newDocumentBuilder();
			var document = documentBuilder.parse(new InputSource(new StringReader(xml)));
			return document
					.getElementsByTagNameNS(CONTENT_NAMESPACE, "content_id")
					.item(0)
					.getTextContent()
					.trim();
		}
		catch (Exception exception) {
			throw new IllegalStateException("Could not read content_id from XML document.", exception);
		}
	}

}
