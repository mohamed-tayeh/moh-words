package com.mohamedtayeh.wosbot.features.anagramHelper;

import com.mohamedtayeh.wosbot.features.anagramHelper.Exceptions.TooManyWildCards;
import com.mohamedtayeh.wosbot.features.constants.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnagramHelper {
    /**
     * Converts a letters to a hash
     *
     * @param letters to convert
     * @return hash of the letters
     */
    public String lettersToHash(String letters) {
        letters = letters.toLowerCase();
        int[] charCount = new int[26];
        for (int i = 0; i < letters.length(); i++) {
            int index = letters.charAt(i) - 'a';
            if (index < 0 || index > 25) {
                continue;
            }
            charCount[index]++;
        }
        return Arrays.toString(charCount);
    }

    /**
     * Wrapper to get all possible hashes given the wild cards
     *
     * @param letters to get the hashes of (can have a wild card)
     * @return list of all possible hashes
     * @throws TooManyWildCards if there
     */
    public List<String> getHashesFromWildCard(String letters) throws TooManyWildCards {

        int wildCardCount = 0;
        for (int i = 0; i < letters.length(); i++) {
            if (letters.charAt(i) == Constants.WILD_CARD) {
                wildCardCount++;
            }
        }

        if (wildCardCount > Constants.MAX_WILD_CARDS) {
            throw new TooManyWildCards("Too many wild cards");
        }

        return getHashesFromWildCardHelper(letters);
    }

    /**
     * Gets all possible hashes given the wild cards
     *
     * @param letters to find the possibilities from
     * @return a list of all possible hashes
     */
    public List<String> getHashesFromWildCardHelper(String letters) {

        List<String> hashes = new ArrayList<>();
        for (int i = 0; i < letters.length(); i++) {
            if (letters.charAt(i) == Constants.WILD_CARD) {
                for (int j = 0; j < 26; j++) {
                    String newLetters = letters.substring(0, i) + (char) (j + 'a') + letters.substring(i + 1);
                    hashes.addAll(getHashesFromWildCardHelper(newLetters));
                }
                return hashes;
            }
        }

        hashes.add(lettersToHash(letters));
        return hashes;
    }

}
