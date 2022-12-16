package com.mohamedtayeh.wosbot.features.subAnagramFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedtayeh.wosbot.features.anagramFile.AnagramFile;
import com.mohamedtayeh.wosbot.features.anagramHelper.AnagramHelper;
import com.mohamedtayeh.wosbot.features.constants.Constants;
import com.mohamedtayeh.wosbot.features.constants.FilePaths;
import com.mohamedtayeh.wosbot.features.subAnagramFile.Exceptions.InvalidSubAnagram;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is responsible for reading and writing to the subAnagrams file
 */
public class SubAnagramFile {
    private final ObjectMapper objectMapper;
    private final AnagramHelper anagramHelper;
    private final AnagramFile anagramFile;
    private final ExecutorService executorService;
    private volatile HashMap<String, HashMap<Integer, TreeSet<String>>> subAnagrams;

    public SubAnagramFile(ObjectMapper objectMapper, AnagramHelper anagramHelper, AnagramFile anagramFile) {
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
     * Adds a word to the subAnagrams file
     *
     * @param word to add
     */
    public void addWord(String word) {
        Runnable runnable = () -> {
            // anagramFile.addWord(word);
            computeSubAnagrams(word);
        };

        executorService.submit(runnable);
    }

    /**
     * Adds anagrams to the subAnagrams file.
     * It merges the anagrams with the existing ones.
     *
     * @param word       the word to add
     * @param subAnagram the subAnagram to add to word
     */
    public void addSubAnagram(String word, String subAnagram) throws InvalidSubAnagram {
        if (!isSubAnagramOfWord(word, subAnagram)) {
            throw new InvalidSubAnagram("The subAnagram " + subAnagram + " is not a subAnagram of " + word);
        }

        String hash = anagramHelper.wordToHash(word);
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
        Runnable runnable = () -> {
            anagramFile.addWords(subAnagramsList);
            computeSubAnagrams(word);
        };

        new Thread(runnable).start();
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

        String hash = anagramHelper.wordToHash(word);
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
        Set<String> subAnagramsSet = anagramFile.getAnagrams(allSubsets(letters));
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

        subAnagrams.put(anagramHelper.wordToHash(letters), subAnagramsByLen);
        // anagramFile.saveFile();
        // saveFile();
    }

    /**
     * Get all possible subsets of letters
     *
     * @param letters to get the subsets from
     * @return list of subsets
     */
    private List<String> allSubsets(String letters) {
        List<String> subsets = new ArrayList<>();
        Stack<String> currSubset = new Stack<>();
        subsetDfs(letters, subsets, currSubset, 0);
        return subsets;
    }

    /**
     * Helper function for allSubsets method
     *
     * @param letters to get the subsets from
     * @param res     the list to add the subsets to
     * @param curr    the current subset
     * @param index   the current index being traversed
     */
    private void subsetDfs(String letters, List<String> res, Stack<String> curr, Integer index) {
        if (index == letters.length() && curr.size() < 4) {
            return;
        }

        if (index == letters.length()) {
            res.add(String.join("", curr));
            return;
        }

        curr.add(String.valueOf(letters.charAt(index)));
        subsetDfs(letters, res, curr, index + 1);

        curr.pop();
        subsetDfs(letters, res, curr, index + 1);
    }

    private Boolean isSubAnagramOfWord(String word, String subAnagram) {
        Map<Character, Integer> wordMap = new HashMap<>();
        Map<Character, Integer> subAnagramMap = new HashMap<>();

        for (char c : word.toCharArray()) {
            wordMap.put(c, wordMap.getOrDefault(c, 0) + 1);
        }

        for (char c : subAnagram.toCharArray()) {
            subAnagramMap.put(c, subAnagramMap.getOrDefault(c, 0) + 1);
        }

        for (char k : wordMap.keySet()) {
            if (wordMap.get(k) > subAnagramMap.getOrDefault(k, 0)) {
                return false;
            }
        }

        return true;

    }

    /**
     * Reads the subAnagrams file
     */
    private void readFile() {
        try {
            subAnagrams = objectMapper.readValue(new File(FilePaths.SUB_ANAGRAM_FILE), new TypeReference<>() {
            });
        } catch (IOException ex) {
            System.out.println("Error reading anagrams file: " + ex.getMessage());
        }
    }

    /**
     * Write new hashmap to file
     */
    public synchronized void saveFile() {
        System.out.println("Saving subAnagrams file");
        try {
            objectMapper.writeValue(new File(FilePaths.SUB_ANAGRAM_FILE), subAnagrams);
        } catch (IOException ex) {
            System.out.println("Error saving anagrams file: " + ex.getMessage());
        }
    }
}