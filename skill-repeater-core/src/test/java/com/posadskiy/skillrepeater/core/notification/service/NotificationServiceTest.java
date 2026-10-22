package com.posadskiy.skillrepeater.core.notification.service;

import com.posadskiy.email.template.api.EmailFormDto;
import com.posadskiy.skillrepeater.core.notification.client.SendEmailOperations;
import com.posadskiy.skillrepeater.core.notification.client.TokenClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationServiceTest {

    @Mock
    private SendEmailOperations sendEmailOperations;

    @Mock
    private TokenClient tokenClient;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationService = new NotificationService(sendEmailOperations, tokenClient, "system", "from-secret", "https://repeaty.posadskiy.com");
    }

    @Test
    void sendNotification_usesClientCredentials() {
        when(tokenClient.token(any())).thenReturn(Map.of("access_token", "test-token"));

        notificationService.sendNotification("test-user", "Java, Spring");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, String>> form = ArgumentCaptor.forClass(Map.class);
        verify(tokenClient).token(form.capture());
        assertEquals("client_credentials", form.getValue().get("grant_type"));
        assertEquals("system", form.getValue().get("client_id"));
        assertEquals("from-secret", form.getValue().get("client_secret"));
        verify(sendEmailOperations).sendBaseTemplatedEmail(org.mockito.ArgumentMatchers.eq("Bearer test-token"), any(EmailFormDto.class));
    }

    @Test
    void sendNotification_rejectsMissingClientSecret() {
        NotificationService unconfigured = new NotificationService(sendEmailOperations, tokenClient, "system", "", "https://repeaty.posadskiy.com");

        assertThrows(IllegalStateException.class, () -> unconfigured.sendNotification("test-user", "Java"));
        verify(tokenClient, never()).token(any());
    }
}
