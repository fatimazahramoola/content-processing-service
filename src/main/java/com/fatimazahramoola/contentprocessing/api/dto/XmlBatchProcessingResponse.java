package com.fatimazahramoola.contentprocessing.api.dto;

import java.util.List;

public record XmlBatchProcessingResponse(
		List<XmlProcessingResponse> results) {
}
