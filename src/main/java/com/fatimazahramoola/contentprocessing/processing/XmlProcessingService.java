package com.fatimazahramoola.contentprocessing.processing;

import com.fatimazahramoola.contentprocessing.api.ProcessingService;
import com.fatimazahramoola.contentprocessing.api.ProcessingStatus;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingRequest;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingResponse;
import org.springframework.stereotype.Service;

@Service
public class XmlProcessingService implements ProcessingService {

	private final XmlValidator xmlValidator;

	public XmlProcessingService(XmlValidator xmlValidator) {
		this.xmlValidator = xmlValidator;
	}

	@Override
	public XmlProcessingResponse process(XmlProcessingRequest request) {
		return xmlValidator.validate(request.xml())
				.map(diagnostic -> new XmlProcessingResponse(
						request.documentName(),
						ProcessingStatus.REJECTED,
						diagnostic))
				.orElseGet(() -> new XmlProcessingResponse(
						request.documentName(),
						ProcessingStatus.ACCEPTED,
						null));
	}

}
