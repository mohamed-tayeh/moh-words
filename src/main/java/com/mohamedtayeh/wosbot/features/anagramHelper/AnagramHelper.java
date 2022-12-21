package com.mohamedtayeh.wosbot.features.anagramHelper;

import com.mohamedtayeh.wosbot.features.anagramHelper.Exceptions.TooManyWildCards;
import com.mohamedtayeh.wosbot.features.constants.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.springframework.stereotype.Service;

@Service
public class AnagramHelper {


  /**
   * Converts a letters to a hash
   *
   * @param letters to convert
   * @return hash of the letters
   */
  public String lettersToHash(String letters) {
    return Arrays.toString(lettersToCharCount(letters));
  }

  /**
   * Wrapper to get all possible hashes given the wild cards
   *
   * @param letters to get the hashes of (can have a wild card)
   * @return list of all possible hashes
   * @throws TooManyWildCards if there are more than expected wild cards
   */
  public List<String> getLettersFromWildCard(String letters) throws TooManyWildCards {

    int wildCardCount = 0;
    for (int i = 0; i < letters.length(); i++) {
      if (letters.charAt(i) == Constants.WILD_CARD) {
        wildCardCount++;
      }
    }

    if (wildCardCount > Constants.MAX_WILD_CARDS) {
      throw new TooManyWildCards("Too many wild cards");
    }

    return getLettersFromWildCardHelper(letters);
  }

  /**
   * Gets all possible hashes given the wild cards
   *
   * @param letters to find the possibilities from
   * @return a list of all possible hashes
   */
  private List<String> getLettersFromWildCardHelper(String letters) {

    List<String> hashes = new ArrayList<>();
    for (int i = 0; i < letters.length(); i++) {
      if (letters.charAt(i) == Constants.WILD_CARD) {
        for (int j = 0; j < 26; j++) {
          String newLetters = letters.substring(0, i) + (char) (j + 'a') + letters.substring(i + 1);
          hashes.addAll(getLettersFromWildCardHelper(newLetters));
        }
        return hashes;
      }
    }

    hashes.add(letters);
    return hashes;
  }

  /**
   * Get all possible subsets of letters
   *
   * @param letters to get the subsets from
   * @return Set of subsets
   */
  public Set<String> allSubsetHashes(String letters) {
    int[] charCount = lettersToCharCount(letters);
    Set<String> subsetHashes = new HashSet<>();
    subsetHashDFS(subsetHashes, charCount, 0, letters.length());
    return subsetHashes;
  }

  private void subsetHashDFS(Set<String> subsetHashes, int[] charCount, int currI,
      int currCharLength) {

    if (currCharLength <= Constants.MIN_WORD_LENGTH - 1) {
      return;
    }

    if (currI >= 26) { // must be >= MIN_WORD_LENGTH
      subsetHashes.add(Arrays.toString(charCount));
      return;
    }

    subsetHashDFS(subsetHashes, charCount, currI + 1, currCharLength);

    if (charCount[currI] > 0) {
      charCount[currI]--;

      if (charCount[currI] > 0) {
        subsetHashDFS(subsetHashes, charCount, currI, currCharLength - 1);
      } else {
        subsetHashDFS(subsetHashes, charCount, currI + 1, currCharLength - 1);
      }

      charCount[currI]++;
    }
  }

  /**
   * Converts a letters to a charCount array
   *
   * @param letters to count the letters of
   * @return charCount of the letters
   */
  private int[] lettersToCharCount(String letters) {
    int[] charCount = new int[26];
    for (int i = 0; i < letters.length(); i++) {
      int index = letters.charAt(i) - 'a';
      if (index < 0 || index > 25) {
        continue;
      }
      charCount[index]++;
    }

    return charCount;
  }

  /**
   * Get all possible subsets of letters
   *
   * @param letters to get the subsets from
   * @return list of subsets
   */
  public List<String> allSubsets(String letters) {
    List<String> subsets = new ArrayList<>();
    Stack<String> currSubset = new Stack<>();
    subsetDfs(letters, subsets, currSubset, 0);
    return subsets;
  }

  /**
   * Returns true if subAnagram can be made from word
   *
   * @param word       to check if subAnagram can be made from
   * @param subAnagram to check if it can be made from word
   * @return true if subAnagram can be made from word, false otherwise
   */
  public Boolean isSubAnagramOfWord(String word, String subAnagram) {
    Map<Character, Integer> wordMap = new HashMap<>();
    Map<Character, Integer> subAnagramMap = new HashMap<>();

    for (char c : word.toCharArray()) {
      wordMap.put(c, wordMap.getOrDefault(c, 0) + 1);
    }

    for (char c : subAnagram.toCharArray()) {
      subAnagramMap.put(c, subAnagramMap.getOrDefault(c, 0) + 1);
    }

    for (char k : wordMap.keySet()) {
      if (wordMap.get(k) < subAnagramMap.getOrDefault(k, 0)) {
        return false;
      }
    }

    return true;
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

}
