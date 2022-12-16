package com.mohamedtayeh.wosbot.scripts;

import com.mohamedtayeh.wosbot.features.anagramFile.AnagramFile;
import com.mohamedtayeh.wosbot.features.subAnagramFile.SubAnagramFile;

import java.util.concurrent.TimeUnit;

public class CreateSubAnagramFile implements Script {
    private final AnagramFile anagramFile;
    private final SubAnagramFile subAnagramFile;

    public CreateSubAnagramFile(AnagramFile anagramFile, SubAnagramFile subAnagramFile) {

        this.anagramFile = anagramFile;
        this.subAnagramFile = subAnagramFile;

    }

    @Override
    public void run() {
        int size = anagramFile.getAnagrams().keySet().size();
        int counter = 0;

        for (String word : anagramFile.getAnagrams().keySet()) {
            counter++;
            subAnagramFile.addWord(word);
            if (counter % 10000 == 0) {
                System.out.println("Progress: " + counter + "/" + size);
            }
        }

        try {
            subAnagramFile.getExecutorService().awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        subAnagramFile.saveFile();
    }


}
