package com.fatimazahramoola.contentprocessing.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fatimazahramoola.contentprocessing.api.dto.XmlProcessingResponse;
import com.fatimazahramoola.contentprocessing.processing.XmlBatchProcessingService;
import com.fatimazahramoola.contentprocessing.publishing.InMemoryArtifactStore;
import com.fatimazahramoola.contentprocessing.publishing.PublishedArtifact;

class XmlProcessingControllerTests {

    private final InMemoryArtifactStore artifactStore = new InMemoryArtifactStore();
    private final XmlBatchProcessingService batchProcessingService = new XmlBatchProcessingService(request -> null, 1);
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new XmlProcessingController(request -> null, batchProcessingService, artifactStore))
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

    @Test
    void processesBatchDocumentsUsingProcessingService() throws Exception {
        List<String> processedDocuments = new CopyOnWriteArrayList<>();
        ProcessingService processingService = request -> {
            processedDocuments.add(request.documentName());
            return new XmlProcessingResponse(request.documentName(), ProcessingStatus.ACCEPTED, null, "{}");
        };
        XmlBatchProcessingService batchProcessingService = new XmlBatchProcessingService(processingService, 2);
        MockMvc batchMockMvc = MockMvcBuilders
                .standaloneSetup(new XmlProcessingController(
                        processingService,
                        batchProcessingService,
                        new InMemoryArtifactStore()))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        try {
            batchMockMvc.perform(post("/api/v1/documents/batch")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "documents": [
                                        {
                                          "documentName": "first.xml",
                                          "xml": "<judgment />"
                                        },
                                        {
                                          "documentName": "second.xml",
                                          "xml": "<judgment />"
                                        }
                                      ]
                                    }
                                    """))
                    .andExpect(status().isAccepted())
                    .andExpect(jsonPath("$.results[0].documentName").value("first.xml"))
                    .andExpect(jsonPath("$.results[0].status").value("ACCEPTED"))
                    .andExpect(jsonPath("$.results[1].documentName").value("second.xml"))
                    .andExpect(jsonPath("$.results[1].status").value("ACCEPTED"));

            assertThat(processedDocuments).containsExactly("first.xml", "second.xml");
        }
        finally {
            batchProcessingService.shutdown();
        }
    }

}
