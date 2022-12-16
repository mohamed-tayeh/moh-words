package com.mohamedtayeh.wosbot.scripts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedtayeh.wosbot.features.anagramFile.AnagramFile;
import com.mohamedtayeh.wosbot.features.anagramHelper.AnagramHelper;
import com.mohamedtayeh.wosbot.features.constants.FilePaths;
import com.mohamedtayeh.wosbot.features.dictionaryApi.DictionaryApi;
import com.mohamedtayeh.wosbot.features.subAnagramFile.SubAnagramFile;

public class RunScript {

    public void run() {
        createSubAnagramFile();
    }

    private void createSubAnagramFile() {
        ObjectMapper objectMapper = new ObjectMapper();
        AnagramHelper anagramHelper = new AnagramHelper();
        AnagramFile anagramFile = new AnagramFile(objectMapper, anagramHelper);
        SubAnagramFile subAnagramFile = new SubAnagramFile(objectMapper,
                anagramHelper, anagramFile);

        Script script = new CreateSubAnagramFile(objectMapper, subAnagramFile, FilePaths.WORDS_FILE);
        script.run();
    }

    private void callDictionaryApi() {
        Script script = new CallDictionaryApi(new DictionaryApi(new ObjectMapper()));
        script.run();
    }


    private void wordsToAnagram() {
        Script wordsToAnagram = new WordsToAnagram(new ObjectMapper(), new
                AnagramHelper(), FilePaths.WORDS_FILE,
                FilePaths.ANAGRAM_FILE);
        wordsToAnagram.run();
    }

}
