package com.mohamedtayeh.wosbot.features.dictionaryApi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedtayeh.wosbot.features.constants.ApiURLS;
import com.mohamedtayeh.wosbot.features.dictionaryApi.responses.Word;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class DictionaryApi {
    private final ObjectMapper objectMapper;

    public DictionaryApi(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<String> getDefinition(String word) {
        if (word == null || word.isEmpty()) {
            return CompletableFuture.supplyAsync(() -> "");
        }

        return queryWord(word).thenApply(Word::getDefinitions);
    }

    public CompletableFuture<Boolean> isWord(String word) {
        if (word == null || word.isEmpty()) {
            return CompletableFuture.supplyAsync(() -> false);
        }

        return queryWord(word).thenApply(Word::isWord);
    }

    private CompletableFuture<Word> queryWord(String word) {
        HttpClient client = HttpClient.newHttpClient();
        String uri = String.format(ApiURLS.DICTIONARY_API, word);
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(uri)).build();

        return client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parse);
    }

    private Word parse(String res) {
        Word[] words = {new Word()};

        try {
            words = objectMapper.readValue(res, Word[].class);
        } catch (JsonProcessingException e) {
//            e.printStackTrace();
            System.out.println("An error occurred when reading the DictionaryApi res: " + e.getMessage());
        }

        return words[0];
    }
}
