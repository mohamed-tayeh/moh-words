package com.mohamedtayeh.wosbot.features.wordApi.responses;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AnagramRes {

  private WordPage[] wordPages;

  /**
   * Returns a list of anagrams
   *
   * @return list of anagrams
   */
  public List<String> getAnagrams() {

    if (wordPages == null || wordPages.length == 0) {
      return new ArrayList<>();
    }

    ArrayList<String> anagrams = new ArrayList<>();
    for (WordPage page : wordPages) {
      for (PossibleWord word : page.getWordList()) {
        anagrams.add(word.getWord());
      }
    }
    return anagrams;
  }

  /**
   * Formats the anagrams by size with labels
   *
   * @return The string format of a list of anagrams
   */
  public String getAnagramsString() {

    if (wordPages == null || wordPages.length == 0) {
      return "";
    }

    StringBuilder sb = new StringBuilder();
    for (WordPage page : wordPages) {
      sb.append('(')
          .append(page.getWordLength())
          .append(')')
          .append(' ');

      for (PossibleWord word : page.getWordList()) {
        sb.append(word.getWord()).append(' ');
      }

      sb.append("|").append(' ');
    }

    sb.deleteCharAt(sb.length() - 2);

    return sb.toString();
  }
}
