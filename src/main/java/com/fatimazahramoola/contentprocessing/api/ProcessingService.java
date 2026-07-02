package com.fatimazahramoola.contentprocessing.api;

import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingRequest;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingResponse;

/**
 * Contract for processing a single XML document through the application pipeline.
 * This keeps HTTP and batch entry points aligned on the same processing behaviour.
 */
public interface ProcessingService {

	/**
	 * Processes one XML document and returns the resulting processing status and output.
	 * Implementations are responsible for preserving the pipeline guarantees.
	 */
	XmlProcessingResponse process(XmlProcessingRequest request);

}
