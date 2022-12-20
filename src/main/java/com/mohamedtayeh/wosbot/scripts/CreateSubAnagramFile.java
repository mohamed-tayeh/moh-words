package com.mohamedtayeh.wosbot.scripts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedtayeh.wosbot.features.constants.Constants;
import com.mohamedtayeh.wosbot.features.constants.FilePaths;
import com.mohamedtayeh.wosbot.features.subAnagramFile.SubAnagramFile;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateSubAnagramFile implements Script {

  private final ObjectMapper objectMapper;
  private final SubAnagramFile subAnagramFile;

  @Override
  public void run() {
    System.out.println("Creating subAnagram file...");
    List<String> words;

    try {
      words = objectMapper.readValue(new File(FilePaths.WORDS_FILE), new TypeReference<>() {
      });
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    for (String word : words) {
      if (word.length() < Constants.MIN_WORD_LENGTH || word.length() > Constants.MAX_WORD_LENGTH) {
        continue;
      }

      subAnagramFile.addWordFromFile(word);
    }

    ExecutorService executorService = subAnagramFile.getExecutorService();
    executorService.shutdown();
    ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);

    scheduledExecutor.scheduleAtFixedRate(() -> {
      System.out.println(
          "Get Completed Tasks: " + ((ThreadPoolExecutor) executorService).getCompletedTaskCount());

      if (executorService.isTerminated()) {
        System.out.println("Done creating subAnagram file...");
        subAnagramFile.saveFile();
        scheduledExecutor.shutdown();
        executorService.shutdown();
      }
    }, 0, 10, TimeUnit.SECONDS);

  }
}
