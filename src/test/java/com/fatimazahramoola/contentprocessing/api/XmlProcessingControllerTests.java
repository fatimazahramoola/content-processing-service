package com.fatimazahramoola.contentprocessing.api;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fatimazahramoola.contentprocessing.publishing.InMemoryArtifactStore;
import com.fatimazahramoola.contentprocessing.publishing.PublishedArtifact;

class XmlProcessingControllerTests {

    private final InMemoryArtifactStore artifactStore = new InMemoryArtifactStore();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new XmlProcessingController(request -> null, artifactStore))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();

    @Test
    void returnsPublishedArtifactByContentId() throws Exception {
        artifactStore.save(new PublishedArtifact(
                "za-gp-2024-001",
                "judgment.xml",
                "{\"content_id\":\"za-gp-2024-001\"}",
                Instant.parse("2024-05-17T10:15:30Z")));

        mockMvc.perform(get("/api/v1/documents/za-gp-2024-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contentId").value("za-gp-2024-001"))
                .andExpect(jsonPath("$.documentName").value("judgment.xml"))
                .andExpect(jsonPath("$.normalizedJson").value("{\"content_id\":\"za-gp-2024-001\"}"))
                .andExpect(jsonPath("$.publishedAt").value("2024-05-17T10:15:30Z"));
    }

    @Test
    void returnsNotFoundWhenPublishedArtifactDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/documents/missing"))
                .andExpect(status().isNotFound());
    }

}
