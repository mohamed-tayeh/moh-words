package com.mohamedtayeh.wosbot.features.subAnagramFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedtayeh.wosbot.features.anagramFile.AnagramFile;
import com.mohamedtayeh.wosbot.features.anagramHelper.AnagramHelper;
import com.mohamedtayeh.wosbot.features.constants.Constants;
import com.mohamedtayeh.wosbot.features.constants.FilePaths;
import com.mohamedtayeh.wosbot.features.subAnagramFile.Exceptions.InvalidSubAnagram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SubAnagramFile {
    private final ObjectMapper objectMapper;
    private final AnagramHelper anagramHelper;
    private final AnagramFile anagramFile;
    private final ExecutorService executorService;
    private volatile HashMap<String, HashMap<Integer, TreeSet<String>>> subAnagrams;

    public SubAnagramFile(@Autowired ObjectMapper objectMapper, @Autowired AnagramHelper anagramHelper, @Autowired AnagramFile anagramFile) {
        this.objectMapper = objectMapper;
        this.anagramHelper = anagramHelper;
        this.anagramFile = anagramFile;
        this.executorService = Executors.newFixedThreadPool(3);
        readFile();

    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * Gets the subAnagrams of a letters
     *
     * @param letters the letters to get the subAnagrams of
     * @return toString of the subAnagrams list
     */
    public String getAnagramsString(String letters, Integer minLength, Integer maxLength) {
        HashMap<Integer, TreeSet<String>> subAnagramsMap = getAnagrams(letters);

        if (subAnagramsMap.isEmpty()) {
            return "";
        }

        Stack<String> anagrams = new Stack<>();

        for (int i = letters.length(); i >= Constants.MIN_WORD_LENGTH; i--) {

            if (!subAnagramsMap.containsKey(i)) {
                continue;
            }

            if (i < minLength || i > maxLength) {
                continue;
            }

            anagrams.add("(" + i + ")");
            anagrams.addAll(subAnagramsMap.get(i));
            anagrams.add("|");
        }

        anagrams.pop(); // the last |

        return String.join(" ", anagrams);
    }

    /**
     * Gets the subAnagrams of a letters
     *
     * @param letters the letters to get the subAnagrams of
     * @return hashmap of anagrams indexed by size
     */
    public HashMap<Integer, TreeSet<String>> getAnagrams(String letters) {

        List<String> possibleHashes = anagramHelper.getHashesFromWildCard(letters);

        List<HashMap<Integer, TreeSet<String>>> hashMapList = new LinkedList<>();

        for (String hash : possibleHashes) {
            if (subAnagrams.containsKey(hash)) {
                hashMapList.add(subAnagrams.get(hash));
            }
        }

        HashMap<Integer, TreeSet<String>> subAnagramsMap = new HashMap<>();

        for (int i = letters.length(); i >= Constants.MIN_WORD_LENGTH; i--) {
            for (HashMap<Integer, TreeSet<String>> hashMap : hashMapList) {

                if (hashMap.containsKey(i)) {

                    if (!subAnagramsMap.containsKey(i)) {
                        subAnagramsMap.put(i, new TreeSet<>());
                    }

                    subAnagramsMap.get(i).addAll(hashMap.get(i));
                }
            }
        }

        return new HashMap<>();
    }

    /**
     * Adds a new word to the subAnagrams file that was
     * not in the word file
     *
     * @param word to add
     */
    public void addWord(String word) {

        executorService.execute(() -> {
            anagramFile.addWord(word);
            computeSubAnagrams(word);
            anagramFile.saveFile();
            saveFile();
        });
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
     * Adds anagrams to the subAnagrams file.
     * It merges the anagrams with the existing ones.
     *
     * @param word       the word to add
     * @param subAnagram the subAnagram to add to word
     */
    public void addSubAnagram(String word, String subAnagram) throws InvalidSubAnagram {
        if (!anagramHelper.isSubAnagramOfWord(word, subAnagram)) {
            throw new InvalidSubAnagram("The subAnagram " + subAnagram + " is not a subAnagram of " + word);
        }

        String hash = anagramHelper.lettersToHash(word);
        HashMap<Integer, TreeSet<String>> subAnagramsMap = subAnagrams.get(hash);

        if (subAnagramsMap == null) {
            subAnagramsMap = new HashMap<>();
        }

        int length = subAnagram.length();
        TreeSet<String> subAnagramsSet = subAnagramsMap.get(length);

        if (subAnagramsSet == null) {
            subAnagramsSet = new TreeSet<>();
        }

        subAnagramsSet.add(subAnagram);
        subAnagramsMap.put(length, subAnagramsSet);
        subAnagrams.put(hash, subAnagramsMap);
    }

    /**
     * Adds anagrams to the subAnagrams file.
     * It merges the anagrams with the existing ones.
     *
     * @param word            the word to add
     * @param subAnagramsList the anagrams for the word
     */
    public void addAnagrams(String word, List<String> subAnagramsList) {
        executorService.execute(() -> {
            anagramFile.addWords(subAnagramsList);
            computeSubAnagrams(word);
            anagramFile.saveFile();
            saveFile();
        });
    }

    /**
     * Checks if it is contains the word
     *
     * @param word to check
     * @return true if it contains the word, false otherwise
     */
    public Boolean containsWord(String word) {
        return anagramFile.containsWord(word);
    }

    /**
     * Checks if the anagram exists for the word
     *
     * @param word    to check
     * @param anagram to check
     * @return true if it contains the anagram, false otherwise
     */
    public Boolean containsSubAnagram(String word, String anagram) {

        String hash = anagramHelper.lettersToHash(word);
        if (subAnagrams.containsKey(hash)) {
            HashMap<Integer, TreeSet<String>> subAnagramsMap = subAnagrams.get(hash);
            Integer length = anagram.length();
            if (subAnagramsMap.containsKey(length)) {
                return subAnagramsMap.get(length).contains(anagram);
            }
        }

        return false;
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

        this.subAnagrams.put(anagramHelper.lettersToHash(letters), subAnagramsByLen);
    }

    /**
     * Reads the subAnagrams file
     */
    private void readFile() {
        try {
            subAnagrams = objectMapper.readValue(new File(FilePaths.SUB_ANAGRAM_FILE), new TypeReference<>() {
            });
        } catch (IOException e) {
            System.out.println("Error reading anagrams file: " + e.getMessage());
        }
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