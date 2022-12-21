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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SubAnagramFile {

  private final ObjectMapper objectMapper;
  private final AnagramHelper anagramHelper;
  private final AnagramFile anagramFile;
  private final ExecutorService executorService = Executors.newFixedThreadPool(3);
  private volatile HashMap<String, HashMap<Integer, TreeSet<String>>> subAnagrams = new HashMap<>();

  /**
   * Gets the executor service
   *
   * @return the executor service
   */
  public ExecutorService getExecutorService() {
    return executorService;
  }

  /**
   * Adds a word from the words file to the subAnagrams file
   *
   * @param word to be added
   */
  public void addWordFromFile(String word) {
    String hash = anagramHelper.lettersToHash(word);

    if (subAnagrams.containsKey(hash)) {
      return;
    }

    subAnagrams.put(hash, new HashMap<>());

    executorService.execute(() -> computeSubAnagrams(word));
  }


  /**
   * Computes the subAnagrams of a letters and adds them to the subAnagrams file
   *
   * @param letters the letters to compute the subAnagrams of
   */
  private void computeSubAnagrams(String letters) {
    Set<String> subAnagramsSet = anagramFile.getAnagrams(anagramHelper.allSubsets(letters));
    HashMap<Integer, TreeSet<String>> subAnagramsByLen = new HashMap<>();

    for (String anagram : subAnagramsSet) {
      Integer length = anagram.length();
      if (subAnagramsByLen.containsKey(length)) {
        subAnagramsByLen.get(length).add(anagram);
        continue;
      }

      TreeSet<String> set = new TreeSet<>();
      set.add(anagram);
      subAnagramsByLen.put(length, set);
    }

    subAnagrams.put(anagramHelper.lettersToHash(letters), subAnagramsByLen);
  }


  /**
   * Write new hashmap to file
   */
  public synchronized void saveFile() {
    try {
      objectMapper.writeValue(new File(FilePaths.SUB_ANAGRAM_FILE), subAnagrams);
    } catch (IOException e) {
      System.out.println("Error saving anagrams file: " + e.getMessage());
    }
  }
}