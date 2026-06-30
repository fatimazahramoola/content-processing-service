package com.fatimazahramoola.contentprocessing.api;

import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingRequest;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingResponse;

public interface ProcessingService {

	XmlProcessingResponse process(XmlProcessingRequest request);

}
