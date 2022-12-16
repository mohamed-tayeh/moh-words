package com.mohamedtayeh.wosbot.features.dictionaryApi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.mohamedtayeh.wosbot.features.constants.ApiURLS;
import com.mohamedtayeh.wosbot.features.dictionaryApi.responses.Word;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DictionaryApi {
    private final ObjectMapper objectMapper;

    public DictionaryApi(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String getDefinition(String word) {
        if (word == null || word.isEmpty()) {
            return new Word().getDefinitions();
        }

        return queryWord(word).getDefinitions();
    }

    public Boolean isWord(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }

        return queryWord(word).isWord();
    }

    private Word queryWord(String word) {
        HttpClient client = HttpClient.newHttpClient();
        String uri = String.format(ApiURLS.DICTIONARY_API, word);
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(uri)).build();

        String res = client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .join();

        return parse(res);
    }

    private Word parse(String res) {
        Word[] words = {new Word()};

        try {
            words = objectMapper.readValue(res, Word[].class);
        } catch (MismatchedInputException e) {
            words = new Word[]{new Word()};
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("An error occurred when reading the DictionaryApi res: " + e.getMessage());
        }

        return words[0];
    }
}
