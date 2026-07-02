package com.fatimazahramoola.contentprocessing.publishing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

/**
 * In-memory store for published artifacts.
 * The store is thread-safe so single and batch processing can publish concurrently.
 */
@Component
public class InMemoryArtifactStore {

	// ConcurrentHashMap keeps the in-memory demo safe under concurrent batch submissions.
	private final ConcurrentMap<String, PublishedArtifact> artifacts = new ConcurrentHashMap<>();

	/**
	 * Saves or replaces an artifact for its content identifier.
	 */
	public PublishedArtifact save(PublishedArtifact artifact) {
		artifacts.put(artifact.contentId(), artifact);
		return artifact;
	}

	/**
	 * Publishes the artifact only when no artifact with the same content identifier exists.
	 * This preserves the first published artifact for idempotent submissions.
	 */
	public PublishedArtifact saveIfAbsent(PublishedArtifact artifact) {
		// putIfAbsent performs the duplicate check and insert atomically.
		PublishedArtifact existingArtifact = artifacts.putIfAbsent(artifact.contentId(), artifact);
		return existingArtifact == null ? artifact : existingArtifact;
	}

	/**
	 * Finds a published artifact by content identifier.
	 */
	public Optional<PublishedArtifact> findByContentId(String contentId) {
		return Optional.ofNullable(artifacts.get(contentId));
	}

	/**
	 * Returns a snapshot of the currently published artifacts.
	 */
	public List<PublishedArtifact> findAll() {
		return new ArrayList<>(artifacts.values());
	}

}
