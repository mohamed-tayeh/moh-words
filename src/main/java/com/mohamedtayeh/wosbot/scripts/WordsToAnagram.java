package com.mohamedtayeh.wosbot.scripts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mohamedtayeh.wosbot.features.anagramHelper.AnagramHelper;
import com.mohamedtayeh.wosbot.features.utils.Constants;
import com.mohamedtayeh.wosbot.features.utils.FilePaths;
import com.mohamedtayeh.wosbot.features.utils.GeneralUtils;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class WordsToAnagram implements Script {

  private final AnagramHelper anagramHelper;

  /**
   * Takes a filePath to a JSON for a word list and converts it
   */
  public Integer call() {
    List<String> words;

    try {
      words = GeneralUtils.objectMapper.readValue(new File(FilePaths.WORDS_FILE),
          new TypeReference<>() {
          });
    } catch (Exception e) {
      e.printStackTrace();
      return 1;
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
      GeneralUtils.objectMapper.writeValue(new File(FilePaths.ANAGRAM_FILE), anagrams);
      return 0;
    } catch (IOException e) {
      log.error("Couldn't save anagram file", e);
      return 1;
    }
  }
}
