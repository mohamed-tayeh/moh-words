package com.mohamedtayeh.wosbot.features.subAnagramFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedtayeh.wosbot.features.anagramFile.AnagramFile;
import com.mohamedtayeh.wosbot.features.anagramHelper.AnagramHelper;
import com.mohamedtayeh.wosbot.features.constants.Constants;
import com.mohamedtayeh.wosbot.features.constants.FilePaths;
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
     * @return hashmap of anagrams indexed by size
     */
    public HashMap<Integer, TreeSet<String>> getAnagrams(String letters) {

        List<String> possibleHashes = anagramHelper.getLettersFromWildCard(letters);

        List<HashMap<Integer, TreeSet<String>>> hashMapList = new LinkedList<>();

        for (String hash : possibleHashes) {
            if (subAnagrams.containsKey(hash)) {
                hashMapList.add(subAnagrams.get(hash));
            }
        }

        HashMap<Integer, TreeSet<String>> subAnagramsMap = new HashMap<>();

        for (HashMap<Integer, TreeSet<String>> hashMap : hashMapList) {
            for (int i = letters.length(); i >= Constants.MIN_WORD_LENGTH; i--) {

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