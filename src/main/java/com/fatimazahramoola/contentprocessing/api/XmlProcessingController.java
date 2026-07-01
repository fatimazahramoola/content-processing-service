package com.fatimazahramoola.contentprocessing.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fatimazahramoola.contentprocessing.api.dto.XmlBatchProcessingRequest;
import com.fatimazahramoola.contentprocessing.api.dto.XmlBatchProcessingResponse;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingRequest;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingResponse;
import com.fatimazahramoola.contentprocessing.processing.XmlBatchProcessingService;
import com.fatimazahramoola.contentprocessing.publishing.InMemoryArtifactStore;
import com.fatimazahramoola.contentprocessing.publishing.PublishedArtifact;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/documents")
public class XmlProcessingController {

	private final ProcessingService processingService;
	private final XmlBatchProcessingService batchProcessingService;
	private final InMemoryArtifactStore artifactStore;

	public XmlProcessingController(
			ProcessingService processingService,
			XmlBatchProcessingService batchProcessingService,
			InMemoryArtifactStore artifactStore) {
		this.processingService = processingService;
		this.batchProcessingService = batchProcessingService;
		this.artifactStore = artifactStore;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.ACCEPTED)
	public XmlProcessingResponse process(@Valid @RequestBody XmlProcessingRequest request) {
		return processingService.process(request);
	}

	@PostMapping("/batch")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public XmlBatchProcessingResponse processBatch(@Valid @RequestBody XmlBatchProcessingRequest request) {
		return batchProcessingService.process(request);
	}

	@GetMapping("/{contentId}")
	public ResponseEntity<PublishedArtifact> findPublishedArtifact(@PathVariable String contentId) {
		return artifactStore.findByContentId(contentId)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

}
