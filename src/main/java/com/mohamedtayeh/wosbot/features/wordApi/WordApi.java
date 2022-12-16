package com.mohamedtayeh.wosbot.features.wordApi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedtayeh.wosbot.features.constants.ApiURLS;
import com.mohamedtayeh.wosbot.features.wordApi.responses.AnagramRes;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

@Service
public class WordApi {
    private final ObjectMapper objectMapper;

    public WordApi(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<AnagramRes> getWords(String word, Integer minLength, Integer maxLength) throws IOException, InterruptedException {
        if (word == null || word.isEmpty()) {
            return CompletableFuture.supplyAsync(AnagramRes::new);
        }
        return queryWord(word, minLength, maxLength);
    }

    private CompletableFuture<AnagramRes> queryWord(String word, Integer minLength, Integer maxLength) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String uri = String.format(ApiURLS.WORD_API, word, minLength, maxLength);
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(uri)).build();
        return client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseRes);
    }

    private AnagramRes parseRes(String res) {
        AnagramRes anagramRes = new AnagramRes();

        try {
            anagramRes = objectMapper.readValue(res, AnagramRes.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("An error occurred when reading the WordApi res: " + e.getMessage());
        }

        return anagramRes;
    }

}
