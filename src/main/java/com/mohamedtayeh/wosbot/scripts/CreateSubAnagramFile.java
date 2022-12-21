package com.mohamedtayeh.wosbot.scripts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedtayeh.wosbot.features.constants.Constants;
import com.mohamedtayeh.wosbot.features.constants.FilePaths;
import com.mohamedtayeh.wosbot.features.subAnagramFile.SubAnagramFile;
import java.io.File;
import java.util.List;
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

    int count = 0;
    int fileNum = 0;
    for (String word : words) {

      if (word.length() < Constants.MIN_WORD_LENGTH || word.length() > Constants.MAX_WORD_LENGTH) {
        continue;
      }

      subAnagramFile.addWordFromFile(word);
      count++;

      if (count % 100000 == 0) {
        fileNum++;
        System.out.println("Saving to file after 100000");
        subAnagramFile.saveFile(fileNum);
      }
    }
  }
}
