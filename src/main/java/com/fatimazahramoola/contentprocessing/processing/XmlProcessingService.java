package com.fatimazahramoola.contentprocessing.processing;

import java.time.Clock;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatimazahramoola.contentprocessing.api.ProcessingService;
import com.fatimazahramoola.contentprocessing.api.ProcessingStatus;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingRequest;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingResponse;
import com.fatimazahramoola.contentprocessing.publishing.InMemoryArtifactStore;
import com.fatimazahramoola.contentprocessing.publishing.PublishedArtifact;

/**
 * Coordinates the XML processing pipeline for a single document.
 * Validation, transformation, metadata extraction and publishing stay in dedicated components.
 */
@Service
public class XmlProcessingService implements ProcessingService {

	private final XmlValidator xmlValidator;
	private final XsltTransformer xsltTransformer;
	private final XmlMetadataExtractor xmlMetadataExtractor;
	private final InMemoryArtifactStore artifactStore;
	private final Clock clock;

	@Autowired
	public XmlProcessingService(
			XmlValidator xmlValidator,
			XsltTransformer xsltTransformer,
			XmlMetadataExtractor xmlMetadataExtractor,
			InMemoryArtifactStore artifactStore) {
		this(xmlValidator, xsltTransformer, xmlMetadataExtractor, artifactStore, Clock.systemUTC());
	}

	XmlProcessingService(
			XmlValidator xmlValidator,
			XsltTransformer xsltTransformer,
			XmlMetadataExtractor xmlMetadataExtractor,
			InMemoryArtifactStore artifactStore,
			Clock clock) {
		this.xmlValidator = xmlValidator;
		this.xsltTransformer = xsltTransformer;
		this.xmlMetadataExtractor = xmlMetadataExtractor;
		this.artifactStore = artifactStore;
		this.clock = clock;
	}

	/**
	 * Validates the XML before transformation and publishing.
	 * Invalid documents return a rejected response and do not produce published artifacts.
	 */
	@Override
	public XmlProcessingResponse process(XmlProcessingRequest request) {
		return xmlValidator.validate(request.xml())
				.map(diagnostic -> new XmlProcessingResponse(
						request.documentName(),
						ProcessingStatus.REJECTED,
						diagnostic,
						null))
				.orElseGet(() -> processValidXml(request));
	}

	private XmlProcessingResponse processValidXml(XmlProcessingRequest request) {
		String normalizedJson = xsltTransformer.transform(request.xml());
		// Metadata is read from validated XML so the service does not depend on the generated JSON shape.
		artifactStore.saveIfAbsent(new PublishedArtifact(
				xmlMetadataExtractor.contentId(request.xml()),
				request.documentName(),
				normalizedJson,
				Instant.now(clock)));
		return new XmlProcessingResponse(
				request.documentName(),
				ProcessingStatus.ACCEPTED,
				null,
				normalizedJson);
	}

}
