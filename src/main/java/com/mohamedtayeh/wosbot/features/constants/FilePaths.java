package com.mohamedtayeh.wosbot.features.constants;

import org.springframework.stereotype.Component;

/**
 * FilePaths used in the application
 */
@Component
public class FilePaths {

  public static final String CHANNELS_FILE_NAME = "src/main/resources/channels.json";
  public static final String CONFIG_FILE_NAME = "src/main/resources/config.json";
  public static final String SUB_ANAGRAM_FILE = "src/main/resources/subAnagrams%d.json";
  public static final String ANAGRAM_FILE = "src/main/resources/anagrams.json";
  public static final String WORDS_FILE = "src/main/resources/words.json";
}
