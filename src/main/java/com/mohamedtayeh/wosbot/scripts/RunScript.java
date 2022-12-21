package com.mohamedtayeh.wosbot.scripts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedtayeh.wosbot.features.anagramFile.AnagramFile;
import com.mohamedtayeh.wosbot.features.anagramHelper.AnagramHelper;
import com.mohamedtayeh.wosbot.features.subAnagramFile.SubAnagramFile;

public class RunScript {

  public void run() {
//    runWordsToAnagram();
    runCreateSubAnagramFile();
  }

  /**
   * Runs the words to anagram script
   */
  public void runWordsToAnagram() {
    ObjectMapper objectMapper = new ObjectMapper();
    AnagramHelper anagramHelper = new AnagramHelper();
    WordsToAnagram wordsToAnagram = new WordsToAnagram(objectMapper, anagramHelper);
    wordsToAnagram.run();
  }

  /**
   * Runs the anagram to subAnagram script
   */
  public void runCreateSubAnagramFile() {
    ObjectMapper objectMapper = new ObjectMapper();
    AnagramHelper anagramHelper = new AnagramHelper();
    AnagramFile anagramFile = new AnagramFile(objectMapper, anagramHelper);
    SubAnagramFile subAnagramFile = new SubAnagramFile(objectMapper, anagramHelper, anagramFile);
    CreateSubAnagramFile createSubAnagramFile = new CreateSubAnagramFile(objectMapper,
        subAnagramFile);
    createSubAnagramFile.run();
  }
}
