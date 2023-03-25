package com.mohamedtayeh.wosbot.features.wordApi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamedtayeh.wosbot.features.utils.ApiURLS;
import com.mohamedtayeh.wosbot.features.utils.GeneralUtils;
import com.mohamedtayeh.wosbot.features.wordApi.responses.AnagramRes;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WordApi {

  /**
   * Parses the API response
   *
   * @param res The API response
   * @return The parsed response
   */
  private static AnagramRes parseRes(String res) {
    AnagramRes anagramRes = new AnagramRes();

    try {
      anagramRes = GeneralUtils.objectMapper.readValue(res, AnagramRes.class);
    } catch (JsonProcessingException e) {
      log.error("An error occurred when reading the WordApi res", e);
    }

    return anagramRes;
  }

  /**
   * Queries the word api for anagrams
   *
   * @param word      The word to get anagrams for
   * @param minLength The minimum length of the anagrams
   * @param maxLength The maximum length of the anagrams
   * @return Completable future of the API response
   */
  private static CompletableFuture<String> queryWord(String word, Integer minLength,
      Integer maxLength) {
    HttpClient client = HttpClient.newHttpClient();
    String uri = String.format(ApiURLS.WORD_API, word, minLength - 1, maxLength + 1);
    HttpRequest req = HttpRequest.newBuilder().uri(URI.create(uri)).build();
    return client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
        .thenApply(HttpResponse::body)
        .thenApply(WordApi::parseRes)
        .thenApply(AnagramRes::getAnagramsString);
  }

  /**
   * Returns a list of anagrams for a given word
   *
   * @param word      The word to get anagrams for
   * @param minLength The minimum length of the anagrams
   * @param maxLength The maximum length of the anagrams
   * @return Completable future of a list of anagrams
   */
  public static CompletableFuture<String> getWords(String word, Integer minLength,
      Integer maxLength) {
    if (word == null || word.isEmpty()) {
      return CompletableFuture.supplyAsync(() -> "");
    }
    return queryWord(word, minLength, maxLength);
  }


}
