package com.mohamedtayeh.wosbot.db.SubAnagram;

import com.mohamedtayeh.wosbot.features.anagramHelper.AnagramHelper;
import com.mohamedtayeh.wosbot.features.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@NonNull
public class SubAnagramController {

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
}
