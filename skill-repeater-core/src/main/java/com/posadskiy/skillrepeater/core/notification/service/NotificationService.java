package com.posadskiy.skillrepeater.core.notification.service;

import com.posadskiy.email.template.api.Button;
import com.posadskiy.email.template.api.Content;
import com.posadskiy.email.template.api.Email;
import com.posadskiy.email.template.api.EmailFormDto;
import com.posadskiy.email.template.api.Recipient;
import com.posadskiy.skillrepeater.core.notification.client.SendEmailOperations;
import com.posadskiy.skillrepeater.core.notification.client.TokenClient;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

import java.util.Map;

@Singleton
public class NotificationService {
    private final SendEmailOperations sendEmailOperations;
    private final TokenClient tokenClient;
    private final String clientId;
    private final String clientSecret;
    private final String publicUrl;

    public NotificationService(
            SendEmailOperations sendEmailOperations,
            TokenClient tokenClient,
            @Value("${auth.client.id:system}") String clientId,
            @Value("${auth.client.secret:}") String clientSecret,
            @Value("${app.public-url:`https://repeaty.posadskiy.com`}") String publicUrl) {
        this.sendEmailOperations = sendEmailOperations;
        this.tokenClient = tokenClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.publicUrl = publicUrl;
    }

    public void sendNotification(String userId, String names) {
        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
            throw new IllegalStateException("Notification client credentials are not configured");
        }
        Map<String, Object> token = tokenClient.token(Map.of(
                "grant_type", "client_credentials",
                "client_id", clientId,
                "client_secret", clientSecret
        ));
        Object accessToken = token.get("access_token");
        if (!(accessToken instanceof String access) || access.isBlank()) {
            throw new IllegalStateException("Auth service did not return an access token");
        }
        sendEmailOperations.sendBaseTemplatedEmail(
                "Bearer " + access,
                new EmailFormDto(
                        new Email("Time to Repeat"),
                        new Recipient(userId),
                        new Content(
                                "skill-repeater service",
                                names + " is waiting to be repeated",
                                "Find a time to train your skill",
                                new Button("Repeated!", publicUrl))));
    }
}
