package com.mohamedtayeh.wosbot.features.dictionaryApi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mohamedtayeh.wosbot.features.constants.ApiURLS;
import com.mohamedtayeh.wosbot.features.dictionaryApi.responses.Word;
import com.mohamedtayeh.wosbot.features.utils.GenericUtils;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DictionaryApi {
  
  /**
   * Gets the definition of a word from the dictionary api
   *
   * @param word the word to get the definition of
   * @return completable future with the definition of the word
   */
  public static CompletableFuture<String> getDefinition(String word) {
    if (word == null || word.isEmpty()) {
      return CompletableFuture.supplyAsync(() -> "");
    }

    return queryWord(word).thenApply(Word::getDefinitions);
  }

  /**
   * Queries the dictionary api to check if a word is a word
   *
   * @param word the word to check
   * @return completable future with a boolean indicated if a word is a word
   */
  public static CompletableFuture<Boolean> isWord(String word) {
    if (word == null || word.isEmpty()) {
      return CompletableFuture.supplyAsync(() -> false);
    }

    return queryWord(word).thenApply(Word::isWord);
  }

  /**
   * Queries the dictionary api and returns its response
   *
   * @param word the word to query
   * @return completable future with the response of the dictionary api
   */
  private static CompletableFuture<Word> queryWord(String word) {
    HttpClient client = HttpClient.newHttpClient();
    String uri = String.format(ApiURLS.DICTIONARY_API, word);
    HttpRequest req = HttpRequest.newBuilder().uri(URI.create(uri)).build();

    return client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
        .thenApply(HttpResponse::body)
        .thenApply(DictionaryApi::parse);
  }

  /**
   * Parses the response of the dictionary api
   *
   * @param res the response of the dictionary api
   * @return the parsed response
   */
  private static Word parse(String res) {
    Word[] words = {new Word()};

    try {
      words = GenericUtils.objectMapper.readValue(res, Word[].class);
    } catch (JsonProcessingException e) {
      System.out.println("An error occurred when reading the DictionaryApi res: " + e.getMessage());
    }

    return words[0];
  }
}
