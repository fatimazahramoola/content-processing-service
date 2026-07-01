package com.fatimazahramoola.contentprocessing.processing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.fatimazahramoola.contentprocessing.api.ProcessingService;
import com.fatimazahramoola.contentprocessing.api.ProcessingStatus;
import com.fatimazahramoola.contentprocessing.api.dto.XmlBatchProcessingRequest;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingRequest;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingResponse;

class XmlBatchProcessingServiceTests {

	@Test
	void processesDocumentsConcurrentlyAndPreservesResponseOrder() {
		CountDownLatch firstTwoDocumentsStarted = new CountDownLatch(2);
		AtomicInteger invocationCount = new AtomicInteger();
		ProcessingService processingService = request -> {
			if (invocationCount.incrementAndGet() <= 2) {
				firstTwoDocumentsStarted.countDown();
				await(firstTwoDocumentsStarted);
			}
			return new XmlProcessingResponse(request.documentName(), ProcessingStatus.ACCEPTED, null, "{}");
		};
		XmlBatchProcessingService batchProcessingService = new XmlBatchProcessingService(processingService, 2);

		try {
			var response = batchProcessingService.process(new XmlBatchProcessingRequest(List.of(
					new XmlProcessingRequest("first.xml", "<judgment />"),
					new XmlProcessingRequest("second.xml", "<judgment />"),
					new XmlProcessingRequest("third.xml", "<judgment />"))));

			assertThat(response.results())
					.extracting(XmlProcessingResponse::documentName)
					.containsExactly("first.xml", "second.xml", "third.xml");
		}
		finally {
			batchProcessingService.shutdown();
		}
	}

	private void await(CountDownLatch latch) {
		try {
			if (!latch.await(2, TimeUnit.SECONDS)) {
				throw new AssertionError("Expected batch documents to be processed concurrently.");
			}
		}
		catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new AssertionError("Interrupted while waiting for concurrent processing.", exception);
		}
	}

}
