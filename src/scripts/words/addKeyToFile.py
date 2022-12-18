import json

## Word file
def wordFile(): 
    with open('../../main/resources/words.json', 'r') as f:
        with open('../../main/resources/wordsDict.json', 'w') as f2:
            wordList = json.load(f)
            count = 0
            size = len(wordList)
            wordDict = {}

            for i, word in enumerate(wordList):

                wordList[i] = {'_id': word}
                if count % 10000 == 0:
                    print(f'Completed {count}/{size}')

                count += 1

            json.dump(subAnagrams, f2)

## Anagrams file
def anagramFile():
    with open('../../main/resources/anagrams.json', 'r') as f:
        with open('../../main/resources/anagramsV2.json', 'w') as f2:
            anagramDict = json.load(f)
            count = 0
            size = len(anagramDict)

            for k, v in anagramDict.items():

                anagramDict[k] = {
                    '_id': k,
                    'value': v
                }

                if count % 10000 == 0:
                    print(f'Completed {count}/{size}')

                count += 1

            json.dump(anagramDict, f2)

## SubAnagrams file
def subAnagramFile():
    with open('../../main/resources/prevSubAnagrams.json', 'r') as f:
        with open('../../main/resources/subAnagramsV2.json', 'w') as f2:
            subAnagrams = json.load(f)
            count = 0
            size = len(subAnagrams)
            for key, v in subAnagrams.items():
                subAnagrams[key] = {
                    'value': v,
                    '_id': key
                }

                if count % 10000 == 0:
                    print(f'Completed {count}/{size}')
                count += 1

            json.dump(subAnagrams, f2)

if __name__ == '__main__':
    # wordFile()
    anagramFile()
    # subAnagramFile()