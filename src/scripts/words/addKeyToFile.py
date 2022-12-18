import json

with open('../../main/resources/prevSubAnagrams.json', 'r') as f:
    with open('../../main/resources/subAnagramsV2.json', 'w') as f2:
        print('reading file...')

        subAnagrams = json.load(f)

        print('read file...')

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