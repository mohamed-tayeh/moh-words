package com.mohamedtayeh.wosbot.db.SubAnagram;

import com.mohamedtayeh.wosbot.db.Anagram.AnagramController;
import com.mohamedtayeh.wosbot.db.SubAnagram.Exceptions.InvalidSubAnagram;
import com.mohamedtayeh.wosbot.features.anagramHelper.AnagramHelper;
import com.mohamedtayeh.wosbot.features.constants.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NonNull
public class SubAnagramController {

  private final AnagramController anagramController;
  private final ExecutorService executorService = Executors.newFixedThreadPool(2);
  private final ExecutorService saveToDb = Executors.newSingleThreadExecutor();
  private SubAnagramRepository subAnagramRepository;
  private AnagramHelper anagramHelper;

  /**
   * Gets all sub anagrams string for a given word as a completable future
   *
   * @param word      the word to get sub anagrams for
   * @param minLength the minimum length of the sub anagrams
   * @param maxLength the maximum length of the sub anagrams
   * @return a completable future of a string of sub anagrams
   */
  public CompletableFuture<String> getSubAnagramsString(String word, Integer minLength,
      Integer maxLength) {

    return CompletableFuture.supplyAsync(() -> {
      Map<String, String> hashToWord = new HashMap<>();

      Set<String> hashes = anagramHelper
          .getLettersFromWildCard(word)
          .stream()
          .map(currWord -> {
            String hash = anagramHelper.lettersToHash(currWord);
            hashToWord.put(hash, currWord);
            return hash;
          })
          .collect(Collectors.toSet());

      HashMap<Integer, TreeSet<String>> subAnagramsMap = new HashMap<>();

      subAnagramRepository
          .findAllById(hashes)
          .forEach(knownSubAnagram -> {
            Map<Integer, TreeSet<String>> subAnagramsByLen = knownSubAnagram.getValue();
            consolidateSubAnagramMap(minLength, word.length(), subAnagramsMap, subAnagramsByLen);
            hashes.remove(knownSubAnagram.getId());
          });

      List<SubAnagram> newSubAnagrams = new ArrayList<>();

      hashes.parallelStream()
          .forEach(hash -> {
            Map<Integer, TreeSet<String>> subAnagramsByLen = getAnagramsByComputation(
                hashToWord.get(hash));
            newSubAnagrams.add(new SubAnagram(hash, subAnagramsByLen));
            consolidateSubAnagramMap(minLength, word.length(), subAnagramsMap, subAnagramsByLen);
          });

      saveToDb.submit(() -> {
        subAnagramRepository.saveAll(newSubAnagrams);
      });

      return subAnagramMapToString(word, minLength, maxLength, subAnagramsMap);
    }, executorService);
  }

  /**
   * To join the subAnagramsByLen into the subAnagramsMap
   *
   * @param minLength        the minimum length of the subAnagrams
   * @param maxLength        the maximum length of the subAnagrams
   * @param subAnagramsMap   the map to be updated
   * @param subAnagramsByLen the map to be joined with the subAnagramsMap
   */
  private synchronized void consolidateSubAnagramMap(Integer minLength, Integer maxLength,
      Map<Integer, TreeSet<String>> subAnagramsMap,
      Map<Integer, TreeSet<String>> subAnagramsByLen) {
    for (int i = maxLength; i >= minLength; i--) {
      if (subAnagramsByLen.containsKey(i)) {

        if (!subAnagramsMap.containsKey(i)) {
          subAnagramsMap.put(i, new TreeSet<>());
        }

        subAnagramsMap.get(i).addAll(subAnagramsByLen.get(i));
      }
    }
  }

  /**
   * Converts the subAnagram map to a string
   *
   * @param word           the word to find subAnagrams for
   * @param minLength      the minimum length of the subAnagrams
   * @param maxLength      the maximum length of the subAnagrams
   * @param subAnagramsMap the map of subAnagrams
   * @return the subAnagrams as a string
   */
  @NotNull
  private String subAnagramMapToString(String word, Integer minLength, Integer maxLength,
      HashMap<Integer, TreeSet<String>> subAnagramsMap) {
    if (subAnagramsMap.isEmpty()) {
      return "";
    }

    StringBuilder sb = new StringBuilder();

    for (int i = word.length(); i >= Constants.MIN_WORD_LENGTH; i--) {

      if (!subAnagramsMap.containsKey(i)) {
        continue;
      }

      if (i < minLength || i > maxLength) {
        continue;
      }

      sb.append('(').append(i).append(')').append(' ');
      subAnagramsMap.get(i).forEach(subAnagram -> sb.append(subAnagram).append(' '));
      sb.append('|').append(' ');
    }

    sb.setLength(sb.length() - 2); // to delete the last " | "
    return sb.toString();
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

  /**
   * Checks if a word is already contianed in the subAnagram repository
   *
   * @param word       to check
   * @param subAnagram to check
   * @return true if the word is contained in the subAnagram, false otherwise
   */
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
   * Adds anagrams to the subAnagrams file. It merges the anagrams with the existing ones.
   *
   * @param word           the word to add
   * @param subAnagramWord the subAnagramWord to add to word
   */
  public void addSubAnagram(String word, String subAnagramWord) throws InvalidSubAnagram {
    if (!anagramHelper.isSubAnagramOfWord(word, subAnagramWord)
        || word.length() == subAnagramWord.length()) {
      throw new InvalidSubAnagram(
          "The subAnagram " + subAnagramWord + " is not a subAnagram of " + word);
    }
    String hash = anagramHelper.lettersToHash(word);
    SubAnagram subAnagram = subAnagramRepository
        .findById(hash)
        .orElseGet(() -> new SubAnagram(hash, new HashMap<>()));

    subAnagram.addSubAnagram(subAnagramWord);
    subAnagramRepository.save(subAnagram);
  }


  /**
   * Adds a new word to the subAnagrams file that was not in the word file
   *
   * @param word to add
   */
  public void addWord(String word) {
    String hash = anagramHelper.lettersToHash(word);
    SubAnagram subAnagram = subAnagramRepository
        .findById(hash)
        .orElseGet(() -> new SubAnagram(hash, new HashMap<>()));

    anagramController.addWord(hash, word);

    if (!subAnagram.getValue().isEmpty()) {
      subAnagram.addSubAnagram(word);
      subAnagramRepository.save(subAnagram);
      return;
    }

    // Thus not in the Anagram collection either
    executorService.execute(() -> {
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

  /**
   * Checks if a word is in the anagrams file, i.e. defined in the custom dictionary
   *
   * @param word to check
   * @return true if the word is in the anagrams file (in our dictionary), false otherwise
   */
  public boolean containsWord(String word) {
    return anagramController.containsWord(word);
  }
}
