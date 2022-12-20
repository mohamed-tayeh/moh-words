package com.mohamedtayeh.wosbot.db.SubAnagram;

import java.util.Collections;
import java.util.Map;
import java.util.TreeSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NonNull
@AllArgsConstructor
public class SubAnagram {

  @Id
  private String id;
  private Map<Integer, TreeSet<String>> value;

  /**
   * Add a new subAnagram to the map
   *
   * @param newWord the new word to add
   */
  public void addSubAnagram(String newWord) {
    int length = newWord.length();

    if (value.containsKey(length)) {
      value.get(length).add(newWord);
      return;
    }

    value.put(length, new TreeSet<>(Collections.singletonList(newWord)));
  }

  /**
   * Checks if a word is in the subAnagram map
   *
   * @param word the word to check
   * @return true if the word is in the map, false otherwise
   */
  public Boolean containsWord(String word) {
    return value.get(word.length()).contains(word);
  }
}
