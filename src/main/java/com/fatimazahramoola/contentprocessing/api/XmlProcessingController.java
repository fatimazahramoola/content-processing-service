package com.fatimazahramoola.contentprocessing.api;

import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingRequest;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/documents")
public class XmlProcessingController {

	private final ProcessingService processingService;

	public XmlProcessingController(ProcessingService processingService) {
		this.processingService = processingService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.ACCEPTED)
	public XmlProcessingResponse process(@Valid @RequestBody XmlProcessingRequest request) {
		return processingService.process(request);
	}

}
