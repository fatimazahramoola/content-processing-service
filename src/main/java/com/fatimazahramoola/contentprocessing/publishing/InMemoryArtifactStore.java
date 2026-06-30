package com.fatimazahramoola.contentprocessing.publishing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Component;

@Component
public class InMemoryArtifactStore {

	private final ConcurrentMap<String, PublishedArtifact> artifacts = new ConcurrentHashMap<>();

	public PublishedArtifact save(PublishedArtifact artifact) {
		artifacts.put(artifact.contentId(), artifact);
		return artifact;
	}

	public Optional<PublishedArtifact> findByContentId(String contentId) {
		return Optional.ofNullable(artifacts.get(contentId));
	}

	public List<PublishedArtifact> findAll() {
		return new ArrayList<>(artifacts.values());
	}

}
