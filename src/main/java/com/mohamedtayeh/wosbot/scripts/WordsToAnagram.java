package com.mohamedtayeh.wosbot.scripts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedtayeh.wosbot.features.anagramHelper.AnagramHelper;
import com.mohamedtayeh.wosbot.features.constants.Constants;
import com.mohamedtayeh.wosbot.features.constants.FilePaths;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WordsToAnagram {

  private final ObjectMapper objectMapper;
  private final AnagramHelper anagramHelper;

  public void run() {
    wordListToAnagramStruct();
  }

  /**
   * Takes a filePath to a JSON for a word list and converts it
   */
  public void wordListToAnagramStruct() {
    List<String> words;

    try {
      words = objectMapper.readValue(new File(FilePaths.WORDS_FILE), new TypeReference<>() {
      });
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    HashMap<String, TreeSet<String>> anagrams = new HashMap<>();

    for (String word : words) {
      if (word.length() > Constants.MAX_WORD_LENGTH || word.length() < Constants.MIN_WORD_LENGTH) {
        continue;
      }

      String hash = anagramHelper.lettersToHash(word);
      if (anagrams.containsKey(hash)) {
        anagrams.get(hash).add(word);
        continue;
      }

      TreeSet<String> set = new TreeSet<>();
      set.add(word);
      anagrams.put(hash, set);
    }

    try {
      objectMapper.writeValue(new File(FilePaths.ANAGRAM_FILE), anagrams);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
