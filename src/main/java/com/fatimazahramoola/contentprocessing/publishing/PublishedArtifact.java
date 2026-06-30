package com.fatimazahramoola.contentprocessing.publishing;

import java.time.Instant;

public record PublishedArtifact(
		String contentId,
		String documentName,
		String normalizedJson,
		Instant publishedAt) {
}
