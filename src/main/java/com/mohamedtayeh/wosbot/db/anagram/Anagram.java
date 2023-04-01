package com.mohamedtayeh.wosbot.db.anagram;

import java.util.TreeSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
public class Anagram {

  @Id
  private String id;
  private TreeSet<String> value;

  /**
   * Adds a value to the anagram
   *
   * @param word to add
   */
  public void addValue(String word) {
    value.add(word);
  }

  /**
   * Checks if the anagram contains the given word
   *
   * @param word to check
   * @return true if the anagram contains the word, false otherwise
   */
  public Boolean containsWord(String word) {
    return value.contains(word);
  }
}
