package com.mohamedtayeh.wosbot.db.SubAnagram;

import com.mohamedtayeh.wosbot.db.Anagram.AnagramController;
import com.mohamedtayeh.wosbot.db.SubAnagram.Exceptions.InvalidSubAnagram;
import com.mohamedtayeh.wosbot.features.anagramHelper.AnagramHelper;
import com.mohamedtayeh.wosbot.features.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@AllArgsConstructor
@NonNull
public class SubAnagramController {
    private final AnagramController anagramController;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private SubAnagramRepository subAnagramRepository;
    private AnagramHelper anagramHelper;

    public String getSubAnagramsString(String word, Integer minLength, Integer maxLength) {
        String hash = anagramHelper.lettersToHash(word);
        Map<Integer, TreeSet<String>> subAnagramsByLen = getSubAnagramByHash(hash);

        if (subAnagramsByLen.isEmpty()) {
            subAnagramsByLen = getAnagramsByComputation(word);
        }

        if (subAnagramsByLen.isEmpty()) {
            return "";
        }

        Stack<String> anagrams = new Stack<>();

        for (int i = word.length(); i >= Constants.MIN_WORD_LENGTH; i--) {

            if (!subAnagramsByLen.containsKey(i)) {
                continue;
            }

            if (i < minLength || i > maxLength) {
                continue;
            }

            anagrams.add("(" + i + ")");
            anagrams.addAll(subAnagramsByLen.get(i));
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
    public Map<Integer, TreeSet<String>> getAnagramsByComputation(String letters) {

        List<String> allSubsetLetters = anagramHelper.allSubsets(letters);

        List<Map<Integer, TreeSet<String>>> hashMapList = new LinkedList<>();

        for (String subset : allSubsetLetters) {
            String hash = anagramHelper.lettersToHash(subset);
            hashMapList.add(getSubAnagramByHash(hash));
        }

        HashMap<Integer, TreeSet<String>> subAnagramsMap = new HashMap<>();

        for (int i = letters.length(); i >= Constants.MIN_WORD_LENGTH; i--) {
            for (Map<Integer, TreeSet<String>> hashMap : hashMapList) {

                if (hashMap.containsKey(i)) {

                    if (!subAnagramsMap.containsKey(i)) {
                        subAnagramsMap.put(i, new TreeSet<>());
                    }

                    subAnagramsMap.get(i).addAll(hashMap.get(i));
                }
            }
        }

        return subAnagramsMap;
    }

    /**
     * @param hash to get the subAnagrams
     * @return Map of subAnagrams by indexed by length
     */
    private Map<Integer, TreeSet<String>> getSubAnagramByHash(String hash) {
        return subAnagramRepository
                .findById(hash)
                .orElseGet(() -> new SubAnagram("", new HashMap<>()))
                .getValue();
    }

    public Boolean containsSubAnagram(String word, String subAnagram) {
        String hash = anagramHelper.lettersToHash(word);

        if (subAnagramRepository.existsById(hash)) {
            return subAnagramRepository
                    .findById(hash)
                    .orElseGet(() -> new SubAnagram("", new HashMap<>()))
                    .containsWord(subAnagram);
        }

        return false;
    }

    /**
     * Adds anagrams to the subAnagrams file.
     * It merges the anagrams with the existing ones.
     *
     * @param word           the word to add
     * @param subAnagramWord the subAnagramWord to add to word
     */
    public void addSubAnagram(String word, String subAnagramWord) throws InvalidSubAnagram {
        if (!anagramHelper.isSubAnagramOfWord(word, subAnagramWord) || word.length() == subAnagramWord.length()) {
            throw new InvalidSubAnagram("The subAnagram " + subAnagramWord + " is not a subAnagram of " + word);
        }

        SubAnagram subAnagram = subAnagramRepository
                .findById(anagramHelper.lettersToHash(word))
                .orElseGet(() -> new SubAnagram(word, new HashMap<>()));

        subAnagram.addSubAnagram(subAnagramWord);
        subAnagramRepository.save(subAnagram);
    }


    /**
     * Adds a new word to the subAnagrams file that was
     * not in the word file
     *
     * @param word to add
     */
    public void addWord(String word) {
        String hash = anagramHelper.lettersToHash(word);
        SubAnagram subAnagram = subAnagramRepository
                .findById(hash)
                .orElseGet(() -> new SubAnagram(word, new HashMap<>()));

        if (!subAnagram.getValue().isEmpty()) {
            subAnagram.addSubAnagram(word);
            return;
        }

        // Thus not in the Anagram collection either
        executorService.execute(() -> {
            anagramController.addWord(word);
            computeSubAnagrams(subAnagram, word);
            subAnagramRepository.save(subAnagram);
        });
    }

    /**
     * Computes the subAnagrams of a letters and adds them to the subAnagrams file
     *
     * @param letters the letters to compute the subAnagrams of
     */
    private void computeSubAnagrams(SubAnagram subAnagram, String letters) {
        anagramController.getAnagrams(
                anagramHelper.allSubsets(letters)
        ).forEach(subAnagram::addSubAnagram);
    }

    public boolean containsWord(String word) {
        return anagramController.containsWord(word);
    }
}
