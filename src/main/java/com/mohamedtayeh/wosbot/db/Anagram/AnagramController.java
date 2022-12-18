package com.mohamedtayeh.wosbot.db.Anagram;

import com.mohamedtayeh.wosbot.features.anagramHelper.AnagramHelper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.TreeSet;

@Service
@AllArgsConstructor
@NonNull
public class AnagramController {
    private AnagramRespository anagramRespository;
    private AnagramHelper anagramHelper;

    public String getAnagramsString(String letters) {
        return String.join(" ", getAnagrams(letters));
    }

    /**
     * Gets anagrams for the given letters
     *
     * @param letters to get anagrams for
     * @return a set of anagrams
     */
    public TreeSet<String> getAnagrams(String letters) {
        return getAnagramsByHash(anagramHelper.lettersToHash(letters));
    }

    private TreeSet<String> getAnagramsByHash(String hash) {
        return anagramRespository
                .findById(hash)
                .orElseGet(() -> new Anagram("", new TreeSet<>()))
                .getValue();
    }
}
