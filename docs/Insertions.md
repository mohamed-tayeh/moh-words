## Insertions

### Inserting a new word ###

- Anagram Collection:
    - Adding it to an existing TreeSet/Sorted list
        - This means that there was another word with the same letters
    - Making a new document
        - This means that there was no other word with the same letters
- SubAnagram Collection:
    - It will not be added to supersets of queries as that would be computationally extremely expensive
    - Adding it to an existing TreeSet/Sorted list:
        - This means that there was another query with the same letters
        - A computation to find all subsets of that word and corresponding subAnagrams was already undertaken
        - Therefore, just need to insert it into the TreeSet/Sorted list
    - Making a new document:
        - First time making the query, therefore have to compute all subsets and retrieve them from  
          the Anagram collection.
        - Can the SubAnagram collection be queried?
            - Yes, but it will have many duplicates in the result that need to
              be handled by the set algorithm
            - Better to use the Anagram collection

### Inserting a new anagram for a word

- Anagram Collection: cannot be possible as that means that word was not in our dictionary,
  therefore it should be added as a word instead of an anagram

- SubAnagram Collection:
    - This means that a word is being added to its superset
    - Therefore, the word.length > anagram.length, otherwise it's not a word in the dictionary
    - If the subAnagram is not in our dictionary:
        - Add it as word
        - Add it as a subAnagram