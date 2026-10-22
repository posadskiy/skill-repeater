package com.posadskiy.skillrepeater.core.notification.client;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

import java.util.Map;

@Client("${auth.service.url}")
public interface TokenClient {

    @Post(uri = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    Map<String, Object> token(@Body Map<String, String> form);
}
