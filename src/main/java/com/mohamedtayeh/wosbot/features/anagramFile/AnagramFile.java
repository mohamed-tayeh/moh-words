package com.mohamedtayeh.wosbot.features.anagramFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedtayeh.wosbot.features.anagramHelper.AnagramHelper;
import com.mohamedtayeh.wosbot.features.constants.FilePaths;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class AnagramFile {
    private final ObjectMapper objectMapper;
    private final AnagramHelper anagramHelper;
    private volatile HashMap<String, Set<String>> anagrams;

    public AnagramFile(ObjectMapper objectMapper, AnagramHelper anagramHelper) {
        this.objectMapper = objectMapper;
        this.anagramHelper = anagramHelper;
        readFile();
    }

    /**
     * Adds a single word to the anagrams file
     *
     * @param word word to add
     */
    public void addWord(String word) {
        String hash = anagramHelper.wordToHash(word);
        if (anagrams.containsKey(hash)) {
            anagrams.get(hash).add(word);
        }
        anagrams.put(hash, new HashSet<>(Collections.singletonList(word)));
    }

    /**
     * Adds multiple words
     *
     * @param words list of words
     */
    public void addWords(List<String> words) {
        words.forEach(this::addWord);
    }

    public String getAnagramsString(String letters) {
        return String.join(" ", new TreeSet<>(getAnagrams(letters)));
    }

    /**
     * Gets anagrams for the given letters
     *
     * @return the anagrams object
     */
    public HashMap<String, Set<String>> getAnagrams() {
        return anagrams;
    }

    /**
     * Gets anagrams for the given letters
     *
     * @param letters to get anagrams for
     * @return a set of anagrams
     */
    public Set<String> getAnagrams(String letters) {
        String hash = anagramHelper.wordToHash(letters);
        if (anagrams.containsKey(hash)) {
            return anagrams.get(hash);
        }
        return new HashSet<>();
    }

    /**
     * Gets a master set for the list of letters
     *
     * @param letters to get anagrams for
     * @return a set of anagrams
     */
    public Set<String> getAnagrams(List<String> letters) {
        return letters.stream()
                .map(this::getAnagrams)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    /**
     * Checks if word is in the file
     *
     * @param word to check
     * @return true if word is in the file, false otherwise
     */
    public Boolean containsWord(String word) {
        String hash = anagramHelper.wordToHash(word);
        if (anagrams.containsKey(hash)) {
            return anagrams.get(hash).contains(word);
        }
        return false;
    }

    /**
     * Reads the anagrams file
     */
    private void readFile() {
        try {
            anagrams = objectMapper.readValue(new File(FilePaths.ANAGRAM_FILE), new TypeReference<>() {
            });
        } catch (IOException ex) {
            System.out.println("Error reading anagrams file: " + ex.getMessage());
        }
    }

    /**
     * Write new hashmap to file
     */
    public synchronized void saveFile() {
        try {
            objectMapper.writeValue(new File(FilePaths.ANAGRAM_FILE), anagrams);
        } catch (IOException ex) {
            System.out.println("Error saving anagrams file: " + ex.getMessage());
        }
    }
}
