## Database Creation

### 3rd Party Resources

Word lists needed to be gathered. Resources:

- Python [NLTK Package](https://www.nltk.org/): ~250k words
- [English GitHub Repo](https://github.com/dwyl/english-words): ~460k words

Python NLTK Package was chosen since it didn't have an overkill number of words.

### Anagram Collection

Algorithm:

1. Create a dictionary (maps hash to a list/set of words)
2. For each word
3. Hash the word based on the number of letters
4. Insert into dictionary's list for each hash

### SubAnagram Collection

Algorithm:

1. Create a dictionary (maps hash to a list/set of words)
2. For each word
3. Hash the word based on the number of letters
4. Calculate all possible subsets of the word (O(2^N))
5. Hash the subsets
6. Query the anagram dictionary for the anagrams
7. Insert into dictionary's list for each hash

Result: for each known word

Limitation:

- End-user can also just give letters that don't form a word
- To return in O(1), this means that a SubAnagram collection would have to be made from every possible combination of
  strings
  between the lengths 4 and 15 (inclusive), which would be very computationally and memory taxing.

Resolution:

- End-user queries for known words, then O(1) time complexity
- Unknown words / just letters, O(2^N) complexity with N in the range of 4 and 15
  inclusive.
- These queries are then stored in the DB for O(1) time complexity in the future


