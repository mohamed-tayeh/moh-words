package com.mohamedtayeh.wosbot.features.constants;

import org.springframework.stereotype.Component;

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

    public static final String WORD_ADDED = "Word has been added to our dictionary @%s!";
    public static final String WORD_EXISTS = "Word is already in the dictionary @%s ;)";
    public static final String WORD_NOT_FOUND = "I couldn't find the word \"%s\" in the dictionary @%s please add it using !addw word-here";
    public static final String NOT_A_WORD = "Looks like \"%s\" is not a valid word in the dictionary @%s 🤔";

    public static final String ADD_ANAGRAM_HELP = "@%s Add an anagram to the word. Usage: !adda word-here anagram-here";
    public static final String NOT_ANAGRAM_IS_WORD = "It seems that we don't have this anagram as a word, so please use !addw word-here instead!";
    public static final String ANAGRAM_ALREADY_EXISTS = "This anagram already exists for this word @%s!";
    public static final String ANAGRAM_ADDED = "Anagram added to the word, thank you @%s!";
    public static final String ANAGRAM_NOT_VALID = "This anagram is not valid for this word @%s!";
    public static final String SUB_ANAGRAM_TOO_LONG = "The anagram is too long @%s!";
}
