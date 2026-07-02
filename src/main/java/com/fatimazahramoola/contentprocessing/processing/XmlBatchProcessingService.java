package com.fatimazahramoola.contentprocessing.processing;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fatimazahramoola.contentprocessing.api.ProcessingService;
import com.fatimazahramoola.contentprocessing.api.dto.XmlBatchProcessingRequest;
import com.fatimazahramoola.contentprocessing.api.dto.XmlBatchProcessingResponse;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingResponse;

import jakarta.annotation.PreDestroy;

/**
 * Coordinates concurrent processing for batch document submissions.
 * Each document is delegated to {@link ProcessingService} so batch and single-document flows share one pipeline.
 */
@Service
public class XmlBatchProcessingService {

	private final ProcessingService processingService;
	private final ExecutorService executorService;

	public XmlBatchProcessingService(
			ProcessingService processingService,
			@Value("${content-processing.batch.concurrency:4}") int concurrency) {
		this.processingService = processingService;
		// A fixed pool is enough for this in-process demo and keeps concurrency externally bounded.
		this.executorService = Executors.newFixedThreadPool(Math.max(1, concurrency));
	}

	/**
	 * Processes all batch documents concurrently and returns results in the same order as the request.
	 */
	public XmlBatchProcessingResponse process(XmlBatchProcessingRequest request) {
		List<Future<XmlProcessingResponse>> futures = request.documents().stream()
				.map(document -> executorService.submit(() -> processingService.process(document)))
				.toList();

		// Futures are consumed in submission order so concurrency does not change the response order.
		List<XmlProcessingResponse> results = futures.stream()
				.map(this::awaitResult)
				.toList();
		return new XmlBatchProcessingResponse(results);
	}

	private XmlProcessingResponse awaitResult(Future<XmlProcessingResponse> future) {
		try {
			return future.get();
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("Batch processing was interrupted.", exception);
		} catch (ExecutionException exception) {
			throw new IllegalStateException("Batch processing failed.", exception);
		}
	}

	/**
	 * Stops the batch executor during application shutdown.
	 */
	@PreDestroy
	public void shutdown() {
		executorService.shutdown();
		try {
			if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
				executorService.shutdownNow();
			}
		} catch (InterruptedException exception) {
			executorService.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

}
