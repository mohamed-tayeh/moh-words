package com.mohamedtayeh.wosbot.scripts;

import com.mohamedtayeh.wosbot.features.anagramFile.AnagramFile;
import com.mohamedtayeh.wosbot.features.subAnagramFile.SubAnagramFile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
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

        // int size = anagramFile.getAnagrams().size();
        int size = 5;

        int counter = 0;
        for (String anagram : anagramFile.getAnagrams().keySet()) {
            if (counter > 4) {
                break;
            }

            subAnagramFile.addWord(anagram);
            counter++;
            System.out.println(counter + "/" + size + " tasks submitted");
        }

        ExecutorService executorService = subAnagramFile.getExecutorService();
        ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);

        scheduledExecutor.scheduleAtFixedRate(() -> {
            long tasksCompleted = ((ThreadPoolExecutor) executorService).getCompletedTaskCount();
            System.out.println(tasksCompleted + "/" + size + " tasks completed");
            if (tasksCompleted == (long) size) {
                subAnagramFile.saveFile();
                scheduledExecutor.shutdown();
                executorService.shutdown();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }
}
