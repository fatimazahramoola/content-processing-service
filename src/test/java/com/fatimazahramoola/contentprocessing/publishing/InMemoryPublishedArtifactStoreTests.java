package com.fatimazahramoola.contentprocessing.publishing;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class InMemoryPublishedArtifactStoreTests {

	private final InMemoryPublishedArtifactStore store = new InMemoryPublishedArtifactStore();

	@Test
	void savesAndFindsArtifactByContentId() {
		PublishedArtifact artifact = new PublishedArtifact(
				"za-gp-2024-001",
				"judgment.xml",
				"{\"content_id\":\"za-gp-2024-001\"}",
				Instant.parse("2024-05-17T10:15:30Z"));

		store.save(artifact);

		assertThat(store.findByContentId("za-gp-2024-001")).contains(artifact);
	}

	@Test
	void returnsEmptyWhenArtifactDoesNotExist() {
		assertThat(store.findByContentId("missing")).isEmpty();
	}

	@Test
	void returnsAllStoredArtifacts() {
		PublishedArtifact first = new PublishedArtifact(
				"za-gp-2024-001",
				"first.xml",
				"{\"content_id\":\"za-gp-2024-001\"}",
				Instant.parse("2024-05-17T10:15:30Z"));
		PublishedArtifact second = new PublishedArtifact(
				"za-gp-2024-002",
				"second.xml",
				"{\"content_id\":\"za-gp-2024-002\"}",
				Instant.parse("2024-05-18T10:15:30Z"));

		store.save(first);
		store.save(second);

		assertThat(store.findAll()).containsExactlyInAnyOrder(first, second);
	}

}
