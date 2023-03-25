package com.mohamedtayeh.wosbot.scripts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mohamedtayeh.wosbot.features.subAnagramFile.SubAnagramFile;
import com.mohamedtayeh.wosbot.features.utils.Constants;
import com.mohamedtayeh.wosbot.features.utils.FilePaths;
import com.mohamedtayeh.wosbot.features.utils.GeneralUtils;
import java.io.File;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CreateSubAnagramFile implements Script {

  private final SubAnagramFile subAnagramFile;

  @Override
  public Integer call() {
    log.info("Creating subAnagram file...");
    List<String> words;

    try {
      words = GeneralUtils.objectMapper.readValue(new File(FilePaths.WORDS_FILE),
          new TypeReference<>() {
          });
    } catch (Exception e) {
      e.printStackTrace();
      return 1;
    }

    int count = 0;
    int fileNum = 0;
    for (String word : words) {

      if (word.length() < Constants.MIN_WORD_LENGTH || word.length() > Constants.MAX_WORD_LENGTH) {
        continue;
      }

      subAnagramFile.addWordFromFile(word);
      count++;

      if (count % 10000 == 0) {
        log.info("Processed {} words", count);
      }
    }
    subAnagramFile.saveFile(fileNum);
    return 0;
  }
}
