package com.mohamedtayeh.wosbot.features.constants;

import org.springframework.stereotype.Component;

/**
 * Constants used in the application
 */
@Component
public class Constants {

  public static final String COMMAND_PREFIX = "!";
  public static final Integer MAX_WORD_LENGTH = 12;
  public static final Integer MIN_WORD_LENGTH = 4;
  public static final Integer MAX_DEFINITIONS = 3;
  public static final Integer MAX_WILD_CARDS = 3;
  public static final Character WILD_CARD = '?';
}
