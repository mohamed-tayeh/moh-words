package com.mohamedtayeh.wosbot.db.Anagram;

import com.mohamedtayeh.wosbot.features.anagramHelper.AnagramHelper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

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

    public Set<String> getAnagrams(List<String> lettersList) {
        return anagramRespository.findAllById(
                        lettersList.stream()
                                .map(anagramHelper::lettersToHash)
                                .collect(Collectors.toList())
                )
                .stream()
                .map(Anagram::getValue)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

    }

    private TreeSet<String> getAnagramsByHash(String hash) {
        return anagramRespository
                .findById(hash)
                .orElseGet(() -> new Anagram("", new TreeSet<>()))
                .getValue();
    }

    public void addWord(String hash, String word) {
        Anagram anagram = anagramRespository
                .findById(hash)
                .orElseGet(() -> new Anagram(hash, new TreeSet<>()));

        anagram.addValue(word);
        anagramRespository.save(anagram);
    }

    /**
     * Checks if word is in the file
     *
     * @param word to check
     * @return true if word is in the file, false otherwise
     */
    public Boolean containsWord(String word) {
        return anagramRespository
                .findById(anagramHelper.lettersToHash(word))
                .orElseGet(() -> new Anagram("", new TreeSet<>()))
                .containsWord(word);
    }

}
