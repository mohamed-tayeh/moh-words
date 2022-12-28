package com.mohamedtayeh.wosbot.features.constants;

import org.springframework.stereotype.Component;

/**
 * Responses used in the application
 */
@Component
public class Responses {

  public static final String WORD_TOO_LONG = "@%s The maximum number of letters allowed is 15!";
  public static final String UNKNOWN_ERROR = "An unknown error occurred! Moh has been notified.";

  public static final String SUB_ANAGRAMS_RES = "All possible (sub)-anagrams of \"%s\" are: %s";
  public static final String SUB_NO_ANAGRAMS_RES = "No (sub)-anagrams found for \"%s\"";

  public static final String ANAGRAMS_RES = "The anagrams of \"%s\" are: %s";
  public static final String NO_ANAGRAMS_RES = "No anagrams found for \"%s\"";

  public static final String DEFINITION_RES = "The definitions of \"%s\" are: %s";
  public static final String NO_DEFINITION_RES = "No definitions found for: \"%s\"";

  public static final String INVALID_LENGTH_PARAM = "@%s The length parameters must be a number!";
  public static final String INVALID_MIN_LENGTH = "@%s The minimum length parameter must be smaller than the word!";
  public static final String INVALID_LENGTH_ORDER = "@%s The minimum length parameter must be smaller than the maximum length parameter!";

  public static final String WORD_ADDED = "Word has been added to our dictionary, thank you @%s!";
  public static final String WORD_EXISTS = "Word is already in the dictionary @%s ;)";
  public static final String NOT_A_WORD = "Looks like \"%s\" is not a valid word in the dictionary @%s 🤔";

  public static final String ONLY_MODS_AND_VIPS = "The bot is configured to only be used by "
      + "broadcaster, mods and vips (:";

  public static final String JOINED_CHANNEL = "I have successfully joined your channel! Make "
      + "sure to mod me to ensure that I can work in followers-only mode and so that I can "
      + "send many messages at once, otherwise Twitch will make me slow ;p\n";
  public static final String ALREADY_JOINED_CHANNEL = "I am already in your channel ;)";
  public static final String LEAVE_CHANNEL =
      "I have left your channel @%s, you can always add me again using !join";
  public static final String ALREADY_LEFT_CHANNEL = "I already left your channel (:";
}
