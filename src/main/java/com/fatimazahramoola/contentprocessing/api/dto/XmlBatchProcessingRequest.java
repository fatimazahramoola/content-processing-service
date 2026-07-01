package com.fatimazahramoola.contentprocessing.api.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record XmlBatchProcessingRequest(
		@NotEmpty List<@Valid XmlProcessingRequest> documents) {
}
