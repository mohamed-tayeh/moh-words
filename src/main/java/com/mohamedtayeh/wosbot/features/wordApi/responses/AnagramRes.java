package com.mohamedtayeh.wosbot.features.wordApi.responses;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AnagramRes {
    private WordPage[] wordPages;

    /**
     * Returns a list of anagrams
     *
     * @return list of anagrams
     */
    public List<String> getAnagrams() {

        if (wordPages == null || wordPages.length == 0) {
            return new ArrayList<>();
        }

        ArrayList<String> anagrams = new ArrayList<>();
        for (WordPage page : wordPages) {
            for (PossibleWord word : page.getWordList()) {
                anagrams.add(word.getWord());
            }
        }
        return anagrams;
    }

    /**
     * Formats the anagrams by size with labels
     *
     * @return The string format of a list of anagrams
     */
    public String getAnagramsString() {

        if (wordPages == null || wordPages.length == 0) {
            return "";
        }

        Stack<String> anagrams = new Stack<>();
        for (WordPage page : wordPages) {
            anagrams.add("(" + page.getWordLength() + ")");
            for (PossibleWord word : page.getWordList()) {
                anagrams.add(word.getWord());
            }
            anagrams.add("|");
        }

        anagrams.pop();

        return String.join(" ", anagrams);
    }
}
