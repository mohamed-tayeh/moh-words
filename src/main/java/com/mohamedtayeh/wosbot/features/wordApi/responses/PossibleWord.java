package com.mohamedtayeh.wosbot.features.wordApi.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PossibleWord implements Comparable<PossibleWord> {

  private String word;

  /**
   * Compares two words
   *
   * @param other the object to be compared.
   * @return a negative integer, zero, or a positive integer as this object is less than, equal to,
   * or greater than the specified object.
   */
  @Override
  public int compareTo(PossibleWord other) {
    return word.compareTo(other.word);
  }
}
