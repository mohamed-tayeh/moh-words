package com.mohamedtayeh.wosbot.db.SubAnagram;

import com.mohamedtayeh.wosbot.db.Anagram.AnagramController;
import com.mohamedtayeh.wosbot.db.SubAnagram.Exceptions.InvalidSubAnagram;
import com.mohamedtayeh.wosbot.features.anagramHelper.AnagramHelper;
import com.mohamedtayeh.wosbot.features.constants.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class SubAnagramController {

  private final ExecutorService executorService = Executors.newSingleThreadExecutor();
  private final AnagramController anagramController;
  private final SubAnagramRepository subAnagramRepository;
  private final AnagramHelper anagramHelper;
  private Set<int[]> primaryKeys;

  public SubAnagramController(AnagramController anagramController,
      SubAnagramRepository subAnagramRepository, AnagramHelper anagramHelper) {
    this.anagramController = anagramController;
    this.subAnagramRepository = subAnagramRepository;
    this.anagramHelper = anagramHelper;
    getPrimaryKeys();
  }

  private void getPrimaryKeys() {
    primaryKeys = new HashSet<>();

    int pageSize = 10000;
    int pageNum = 0;
    boolean gotAllKeys = false;

    while (!gotAllKeys) {
      Page<SubAnagram> page = subAnagramRepository.findAll(PageRequest.of(pageNum, pageSize));
      page.forEach(
          subAnagram -> primaryKeys.add(anagramHelper.hashToCharCount(subAnagram.getId())));

      gotAllKeys = !page.hasNext();
      pageNum++;
    }
  }

  /**
   * Checks if a word is already contained in the subAnagram repository
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
   * Checks if a word is in the anagrams file, i.e. defined in the custom dictionary
   *
   * @param word to check
   * @return true if the word is in the anagrams file (in our dictionary), false otherwise
   */
  public boolean containsWord(String word) {
    return anagramController.containsWord(word);
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

    executorService.submit(() -> {
      int[] charCount = anagramHelper.lettersToCharCount(word);

      List<String> keysToUpdate = new ArrayList<>();

      for (int[] primaryKey : primaryKeys) {
        if (anagramHelper.isSubAnagramOfCharCount(primaryKey, charCount)) {
          keysToUpdate.add(Arrays.toString(primaryKey));
        }
      }

      subAnagramRepository.saveAll(
          subAnagramRepository.findAllById(keysToUpdate)
              .stream()
              .peek(superSet -> superSet.addSubAnagram(word)).toList());

      primaryKeys.add(charCount);
    });

    if (!subAnagram.getValue().isEmpty()) { // there has been a query with this word
      subAnagram.addSubAnagram(word);
      subAnagramRepository.save(subAnagram);
      return;
    }

    // calculate all possible sub anagrams
    executorService.execute(() -> {
      computeSubAnagrams(subAnagram, word);
      subAnagramRepository.save(subAnagram);
    });

  }

  /**
   * Computes the subAnagrams of a letters and adds them to the subAnagram object passed in
   *
   * @param subAnagram the subAnagram object in the database
   * @param letters    the letters to compute the subAnagrams of
   */
  private void computeSubAnagrams(SubAnagram subAnagram, String letters) {
    subAnagram.setValue(
        anagramHelper.computeSubAnagrams(letters, anagramController::getAnagramsByHashes));
  }

  /**
   * Gets all sub anagrams string for a given letters as a completable future
   *
   * @param letters   the letters to get sub anagrams for
   * @param minLength the minimum length of the sub anagrams
   * @param maxLength the maximum length of the sub anagrams
   * @return a completable future of a string of sub anagrams
   */
  public CompletableFuture<String> getSubAnagramsString(String letters, Integer minLength,
      Integer maxLength) {

    return CompletableFuture.supplyAsync(() -> {
      Map<String, String> hashToWord = new HashMap<>();

      Set<String> hashes = anagramHelper
          .getLettersFromWildCard(letters)
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
            consolidateSubAnagramMap(minLength, letters.length(), subAnagramsMap,
                knownSubAnagram.getValue());
            hashes.remove(knownSubAnagram.getId());
          });

      List<SubAnagram> newSubAnagrams = new ArrayList<>();

      hashes.parallelStream()
          .forEach(hash -> {
            Map<Integer, TreeSet<String>> subAnagramsByLen = anagramHelper.computeSubAnagrams(
                hashToWord.get(hash), anagramController::getAnagramsByHashes);
            newSubAnagrams.add(new SubAnagram(hash, subAnagramsByLen));
            primaryKeys.add(anagramHelper.hashToCharCount(hash));
            consolidateSubAnagramMap(minLength, letters.length(), subAnagramsMap, subAnagramsByLen);
          });

      executorService.submit(() -> {
        subAnagramRepository.saveAll(newSubAnagrams);
      });

      return subAnagramMapToString(letters, minLength, maxLength, subAnagramsMap);
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
}
