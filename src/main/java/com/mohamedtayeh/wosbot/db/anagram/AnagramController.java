package com.mohamedtayeh.wosbot.db.anagram;

import com.mohamedtayeh.wosbot.features.anagramHelper.AnagramHelper;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NonNull
public class AnagramController {

  private AnagramRepository anagramRepository;
  private AnagramHelper anagramHelper;

  /**
   * Returns a string of the anagrams of the given letters
   *
   * @param letters to find anagrams for
   * @return string of anagrams
   */
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

    return anagramRepository
        .findById(anagramHelper.lettersToHash(letters))
        .orElseGet(() -> new Anagram("", new TreeSet<>()))
        .getValue();
  }

  /**
   * Gets anagrams for the given letters list
   *
   * @param lettersList to get anagrams for
   * @return a set of anagrams
   */
  public Set<String> getAnagrams(List<String> lettersList) {
    return anagramRepository.findAllById(
            lettersList.stream()
                .map(anagramHelper::lettersToHash)
                .collect(Collectors.toList())
        )
        .stream()
        .map(Anagram::getValue)
        .flatMap(Set::stream)
        .collect(Collectors.toSet());

  }

  /**
   * Gets anagrams for the given letters list
   *
   * @param hashes hashes to get anagrams for
   * @return a set of anagrams
   */
  public Set<String> getAnagramsByHashes(Set<String> hashes) {
    return anagramRepository.findAllById(hashes)
        .stream()
        .map(Anagram::getValue)
        .flatMap(Set::stream)
        .collect(Collectors.toSet());
  }

  /**
   * Adds a word to the database, either by adding it to an existing anagram or by creating a new
   * one
   *
   * @param hash of the anagram
   * @param word to add
   */
  public void addWord(String hash, String word) {
    Anagram anagram = anagramRepository
        .findById(hash)
        .orElseGet(() -> new Anagram(hash, new TreeSet<>()));

    anagram.addValue(word);
    anagramRepository.save(anagram);
  }

  /**
   * Checks if word is in the file
   *
   * @param word to check
   * @return true if word is in the file, false otherwise
   */
  public Boolean containsWord(String word) {
    return anagramRepository
        .findById(anagramHelper.lettersToHash(word))
        .orElseGet(() -> new Anagram("", new TreeSet<>()))
        .containsWord(word);
  }

}
