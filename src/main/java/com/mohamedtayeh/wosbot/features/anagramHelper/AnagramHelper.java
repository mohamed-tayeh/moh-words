package com.mohamedtayeh.wosbot.features.anagramHelper;

import com.mohamedtayeh.wosbot.features.anagramHelper.exceptions.TooManyWildCards;
import com.mohamedtayeh.wosbot.features.utils.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
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
   * Converts a letters to a hash
   *
   * @param hash to convert
   * @return char count of the hash
   */
  public int[] hashToCharCount(String hash) {

    String[] hashArr = hash.substring(1, hash.length() - 1).split(", ");
    int[] charCount = new int[26];
    for (int i = 0; i < 26; i++) {
      charCount[i] = Integer.parseInt(hashArr[i]);
    }

    return charCount;
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
   * Computes the subAnagrams of a letters and adds them to the subAnagrams file
   *
   * @param letters             the letters to compute the subAnagrams of
   * @param getAnagramsByHashes to get the anagrams by the hashes
   * @return computed map of subAnagrams
   */
  public HashMap<Integer, TreeSet<String>> computeSubAnagrams(String letters,
      Function<Set<String>, Set<String>> getAnagramsByHashes) {
    Set<String> subAnagramsSet = getAnagramsByHashes.apply(allSubsetHashes(letters));
    HashMap<Integer, TreeSet<String>> subAnagramsByLen = new HashMap<>();

    for (String anagram : subAnagramsSet) {
      Integer length = anagram.length();
      if (subAnagramsByLen.containsKey(length)) {
        subAnagramsByLen.get(length).add(anagram);
        continue;
      }

      TreeSet<String> treeSet = new TreeSet<>();
      treeSet.add(anagram);
      subAnagramsByLen.put(length, treeSet);
    }

    return subAnagramsByLen;
  }

  /**
   * Returns true if charCount is a subAnagram of primaryKey
   *
   * @param primaryKey the primary key to check if charCount is a subAnagram of
   * @param charCount  the charCount to check if it is a subAnagram of primaryKey
   * @return true if charCount is a subAnagram of primaryKey, false otherwise
   */
  public Boolean isSubAnagramOfCharCount(int[] primaryKey, int[] charCount) {
    for (int i = 0; i < 26; i++) {
      if (primaryKey[i] < charCount[i]) {
        return false;
      }
    }

    return true;
  }

  /**
   * Converts a letters to a charCount array
   *
   * @param letters to count the letters of
   * @return charCount of the letters
   */
  public int[] lettersToCharCount(String letters) {
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

    if (currI >= 26) { // must be >= MIN_WORD_LENGTH b/c of first if statement
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
}
