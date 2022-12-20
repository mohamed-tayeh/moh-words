package com.mohamedtayeh.wosbot.features.wordApi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedtayeh.wosbot.features.constants.ApiURLS;
import com.mohamedtayeh.wosbot.features.wordApi.responses.AnagramRes;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WordApi {

  private final ObjectMapper objectMapper;

  /**
   * Returns a list of anagrams for a given word
   *
   * @param word      The word to get anagrams for
   * @param minLength The minimum length of the anagrams
   * @param maxLength The maximum length of the anagrams
   * @return Completable future of a list of anagrams
   */
  public CompletableFuture<String> getWords(String word, Integer minLength, Integer maxLength) {
    if (word == null || word.isEmpty()) {
      return CompletableFuture.supplyAsync(() -> "");
    }
    return queryWord(word, minLength, maxLength);
  }

  /**
   * Queries the word api for anagrams
   *
   * @param word      The word to get anagrams for
   * @param minLength The minimum length of the anagrams
   * @param maxLength The maximum length of the anagrams
   * @return Completable future of the API response
   */
  private CompletableFuture<String> queryWord(String word, Integer minLength,
      Integer maxLength) {
    HttpClient client = HttpClient.newHttpClient();
    String uri = String.format(ApiURLS.WORD_API, word, minLength - 1, maxLength + 1);
    HttpRequest req = HttpRequest.newBuilder().uri(URI.create(uri)).build();
    return client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
        .thenApply(HttpResponse::body)
        .thenApply(this::parseRes)
        .thenApply(AnagramRes::getAnagramsString);
  }

  /**
   * Parses the API response
   *
   * @param res The API response
   * @return The parsed response
   */
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
