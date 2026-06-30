package com.fatimazahramoola.contentprocessing.api.dto;

import com.fatimazahramoola.contentprocessing.api.ProcessingStatus;

public record XmlProcessingResponse(
		String documentName,
		ProcessingStatus status,
		String diagnostic) {
}
