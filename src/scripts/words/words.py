# import nltk
# nltk.download()
import json
from nltk.corpus import words
word_list = words.words()


for i, word in enumerate(word_list):
    word_list[i] = word.lower()

with open('../../main/resources/words.json', 'w') as f:
    json.dump(word_list, f)
