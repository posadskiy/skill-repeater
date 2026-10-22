package com.posadskiy.skillrepeater.web;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.EnabledIf;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(transactional = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateSkillHttpTest implements TestPropertyProvider {

    private static final String SECRET = "0123456789abcdef0123456789abcdef";

    private static PostgreSQLContainer<?> postgres;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    @Client("/")
    HttpClient client;

    @Override
    public Map<String, String> getProperties() {
        if (!dockerAvailable()) {
            return Map.of();
        }
        postgres = new PostgreSQLContainer<>("postgres:16-alpine");
        postgres.start();
        return Map.of(
                "datasources.default.url", postgres.getJdbcUrl(),
                "datasources.default.username", postgres.getUsername(),
                "datasources.default.password", postgres.getPassword(),
                "datasources.default.driver-class-name", "org.postgresql.Driver",
                "datasources.default.dialect", "POSTGRES",
                "datasources.default.schema-generate", "NONE",
                "datasources.default.maximum-pool-size", "2",
                "flyway.datasources.default.enabled", "true",
                "micronaut.security.token.jwt.signatures.secret.validation.secret", SECRET
        );
    }

    @Test
    void createPayloadDeserializes() throws Exception {
        String json = """
                {"name":"Test","description":"Wanan test","period":"DAYS","number":1,"priority":"MEDIUM","learnNow":false}
                """;
        byte[] bytes = objectMapper.writeValueAsBytes(objectMapper.readValue(json, com.posadskiy.skillrepeater.api.dto.SkillDto.class));
        assertTrue(bytes.length > 0);
    }

    @Test
    @EnabledIf("dockerAvailable")
    void createSkillReturnsAUsefulResponse() {
        String body = """
                {"name":"Test","description":"Wanan test","period":"DAYS","number":1,"priority":"MEDIUM","learnNow":false}
                """;
        HttpRequest<?> request = HttpRequest.POST("/v0/skills", body)
                .bearerAuth(token())
                .contentType("application/json");
        HttpResponse<String> response = client.toBlocking().exchange(request, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.body().contains("Test"));
    }

    @Test
    @EnabledIf("dockerAvailable")
    void invalidSkillExplainsWhatToFix() {
        String body = """
                {"name":"","description":"Wanan test","period":"DAYS","number":0,"priority":"MEDIUM","learnNow":false}
                """;
        HttpRequest<?> request = HttpRequest.POST("/v0/skills", body)
                .bearerAuth(token())
                .contentType("application/json");
        HttpClientResponseException exception = org.junit.jupiter.api.Assertions.assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, String.class)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        String responseBody = exception.getResponse().getBody(String.class).orElse("");
        assertTrue(responseBody.contains("application/problem+json") || responseBody.contains("\"title\":\"Validation failed\""));
        assertTrue(responseBody.contains("must not be blank") || responseBody.contains("must be greater than or equal to 1"));
    }

    private static String token() {
        long now = Instant.now().getEpochSecond();
        String header = base64("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = base64("{\"sub\":\"user-1\",\"iss\":\"https://auth.test\",\"aud\":\"api.test\",\"exp\":" + (now + 3600) + ",\"nbf\":" + (now - 60) + "}");
        String signingInput = header + "." + payload;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return signingInput + "." + base64(mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static String base64(String value) {
        return base64(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String base64(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    static boolean dockerAvailable() {
        try {
            return DockerClientFactory.instance().isDockerAvailable();
        } catch (RuntimeException exception) {
            return false;
        }
    }
}
