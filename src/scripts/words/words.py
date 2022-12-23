import json
from nltk.corpus import words
import wn

def load_words(option: str, asSet = False) -> set:
    if option == "english-words":
        with open('words_alpha.txt') as word_file:
            if asSet:
                valid_words = set(word_file.read().split())
            else:
                valid_words = word_file.read().split()
        return valid_words
    elif option == "wordnet":
        en = wn.Wordnet('oewn:2021')
        if asSet:
            wnWords = {word.id[5:len(word.id) - 2] for word in en.words()}
        else:
            wnWords = [word.id[5:len(word.id) - 2] for word in en.words()]
        return wnWords
    elif option == "nltk":
        if asSet:
            nltkWords = set(words.words())
        else:
            nltkWords = words.words()
        return nltkWords
    else:
        raise ValueError("Invalid option")

if __name__ == "__main__":
    word_list = load_words("english-words")
    with open('../../main/resources/words.json', 'w') as f:
        json.dump(word_list, f)
