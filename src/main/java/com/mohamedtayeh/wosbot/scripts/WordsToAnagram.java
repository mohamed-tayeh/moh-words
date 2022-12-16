package com.mohamedtayeh.wosbot.scripts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedtayeh.wosbot.features.anagramHelper.AnagramHelper;
import com.mohamedtayeh.wosbot.features.constants.Constants;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordsToAnagram implements Script {
    private final ObjectMapper objectMapper;
    private final AnagramHelper anagramHelper;
    private final String sourcePath;
    private final String destPath;

    public WordsToAnagram(ObjectMapper objectMapper, AnagramHelper anagramHelper, String sourcePath, String destPath) {
        this.objectMapper = objectMapper;
        this.anagramHelper = anagramHelper;
        this.sourcePath = sourcePath;
        this.destPath = destPath;
    }

    @Override
    public void run() {
        wordListToAnagramStruct();
    }

    /**
     * Takes a filePath to a JSON for a word list and converts it
     */
    public void wordListToAnagramStruct() {
        List<String> words;

        try {
            words = objectMapper.readValue(new File(sourcePath), new TypeReference<>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        HashMap<String, Set<String>> anagrams = new HashMap<>();

        for (String word : words) {
            if (word.length() > Constants.MAX_WORD_LENGTH || word.length() < Constants.MIN_WORD_LENGTH) {
                continue;
            }

            String hash = anagramHelper.lettersToHash(word);
            if (anagrams.containsKey(hash)) {
                anagrams.get(hash).add(word);
                continue;
            }

            Set<String> set = new HashSet<>();
            set.add(word);
            anagrams.put(hash, set);
        }

        try {
            objectMapper.writeValue(new File(destPath), anagrams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
