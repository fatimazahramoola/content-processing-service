package com.fatimazahramoola.contentprocessing.api.dto;

import jakarta.validation.constraints.NotBlank;

public record XmlProcessingRequest(
		@NotBlank String documentName,
		@NotBlank String xml) {
}
