package com.fatimazahramoola.contentprocessing.processing;

import com.fatimazahramoola.contentprocessing.api.ProcessingService;
import com.fatimazahramoola.contentprocessing.api.ProcessingStatus;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingRequest;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingResponse;
import org.springframework.stereotype.Service;

@Service
public class XmlProcessingService implements ProcessingService {

	private final XmlValidator xmlValidator;
	private final XsltTransformer xsltTransformer;

	public XmlProcessingService(XmlValidator xmlValidator, XsltTransformer xsltTransformer) {
		this.xmlValidator = xmlValidator;
		this.xsltTransformer = xsltTransformer;
	}

	@Override
	public XmlProcessingResponse process(XmlProcessingRequest request) {
		return xmlValidator.validate(request.xml())
				.map(diagnostic -> new XmlProcessingResponse(
						request.documentName(),
						ProcessingStatus.REJECTED,
						diagnostic,
						null))
				.orElseGet(() -> new XmlProcessingResponse(
						request.documentName(),
						ProcessingStatus.ACCEPTED,
						null,
						xsltTransformer.transform(request.xml())));
	}

}
