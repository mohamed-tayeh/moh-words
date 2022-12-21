package com.mohamedtayeh.wosbot.features.subAnagramFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedtayeh.wosbot.features.anagramFile.AnagramFile;
import com.mohamedtayeh.wosbot.features.anagramHelper.AnagramHelper;
import com.mohamedtayeh.wosbot.features.constants.FilePaths;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SubAnagramFile {

  private final ObjectMapper objectMapper;
  private final AnagramHelper anagramHelper;
  private final AnagramFile anagramFile;
  private volatile HashMap<String, HashMap<Integer, TreeSet<String>>> subAnagrams = new HashMap<>();
  
  /**
   * Adds a word from the words file to the subAnagrams file
   *
   * @param word to be added
   */
  public void addWordFromFile(String word) {
    String hash = anagramHelper.lettersToHash(word);

    if (subAnagrams.containsKey(hash)) {
      return; // already calculated or in progress of calculating the angarams of this word
    }

    subAnagrams.put(hash, new HashMap<>());
    computeSubAnagrams(word);
  }

  /**
   * Computes the subAnagrams of a letters and adds them to the subAnagrams file
   *
   * @param letters the letters to compute the subAnagrams of
   */
  private void computeSubAnagrams(String letters) {
    Set<String> subAnagramsSet = anagramFile.getAnagramsByHashes(
        anagramHelper.allSubsetHashes(letters));
    HashMap<Integer, TreeSet<String>> subAnagramsByLen = new HashMap<>();

    for (String anagram : subAnagramsSet) {
      Integer length = anagram.length();
      if (subAnagramsByLen.containsKey(length)) {
        subAnagramsByLen.get(length).add(anagram);
        continue;
      }

      TreeSet<String> treeSet = new TreeSet<>();
      treeSet.add(anagram);
      subAnagramsByLen.put(length, treeSet);
    }

    subAnagrams.put(anagramHelper.lettersToHash(letters), subAnagramsByLen);
  }

  /**
   * Write new hashmap to file
   */
  public void saveFile(int fileNum) {
    try {
      objectMapper.writeValue(new File(String.format(FilePaths.SUB_ANAGRAM_FILE, fileNum)),
          subAnagrams);
      subAnagrams = new HashMap<>();
    } catch (IOException e) {
      System.out.println("Error saving anagrams file: " + e.getMessage());
    }
  }
}