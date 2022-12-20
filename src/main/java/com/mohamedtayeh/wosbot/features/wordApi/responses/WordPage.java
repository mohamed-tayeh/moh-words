package com.mohamedtayeh.wosbot.features.wordApi.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Arrays;
import lombok.Data;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class WordPage {

  private PossibleWord[] wordList;
  @JsonProperty("length")
  private Integer wordLength;

  /**
   * Sorts the response word list
   *
   * @note this is used internally by Jaskson to sort the response
   */
  public void setWordList(PossibleWord[] wordList) {
    this.wordList = wordList;
    Arrays.sort(this.wordList);
  }
}
