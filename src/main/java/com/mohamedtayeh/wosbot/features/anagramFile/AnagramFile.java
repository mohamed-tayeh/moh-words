package com.mohamedtayeh.wosbot.features.anagramFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedtayeh.wosbot.features.anagramHelper.AnagramHelper;
import com.mohamedtayeh.wosbot.features.constants.FilePaths;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AnagramFile {

  private final ObjectMapper objectMapper;
  private final AnagramHelper anagramHelper;
  private volatile HashMap<String, Set<String>> anagrams;

  /**
   * Gets anagrams for the given letters
   *
   * @return the anagrams object
   */
  public HashMap<String, Set<String>> getAnagrams() {
    return anagrams;
  }

  /**
   * Gets anagrams for the given letters
   *
   * @param letters to get anagrams for
   * @return a set of anagrams
   */
  public Set<String> getAnagrams(String letters) {
    String hash = anagramHelper.lettersToHash(letters);
    if (anagrams.containsKey(hash)) {
      return anagrams.get(hash);
    }
    return new HashSet<>();
  }

  /**
   * Gets a master set for the list of letters
   *
   * @param letters to get anagrams for
   * @return a set of anagrams
   */
  public Set<String> getAnagrams(List<String> letters) {
    return letters.stream()
        .map(this::getAnagrams)
        .flatMap(Set::stream)
        .collect(Collectors.toSet());
  }

  /**
   * Reads the anagrams file
   */
  @PostConstruct
  private void readFile() {
    try {
      anagrams = objectMapper.readValue(new File(FilePaths.ANAGRAM_FILE), new TypeReference<>() {
      });
    } catch (IOException ex) {
      System.out.println("Error reading anagrams file: " + ex.getMessage());
    }
  }

  /**
   * Write new hashmap to file
   */
  public synchronized void saveFile() {
    try {
      objectMapper.writeValue(new File(FilePaths.ANAGRAM_FILE), anagrams);
    } catch (IOException ex) {
      System.out.println("Error saving anagrams file: " + ex.getMessage());
    }
  }
}
