package com.mohamedtayeh.wosbot.features.dictionaryApi.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mohamedtayeh.wosbot.features.utils.Constants;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Word {

  private String word = "";
  private Meaning[] meanings = {};

  /**
   * Returns if the word is valid or not
   *
   * @return true if the word is valid, false otherwise
   */
  public Boolean isWord() {
    return !word.isEmpty();
  }

  /**
   * Returns the word
   *
   * @return the word
   */
  public String getWord() {
    return word;
  }

  /**
   * Gets the first 3 definitions of the word
   *
   * @return the first 3 definitions of the word
   */
  public String getDefinitions() {
    StringBuilder definitions = new StringBuilder();
    int numDefinitions = 0;

    for (Meaning meaning : meanings) {
      for (Definition definition : meaning.getDefinitions()) {
        definitions
            .append("(")
            .append(numDefinitions + 1)
            .append(") ")
            .append(definition.getDefinition())
            .append(" ");
        numDefinitions++;
        if (numDefinitions >= Constants.MAX_DEFINITIONS) {
          return definitions.toString();
        }
      }
    }

    return definitions.toString();
  }
}
