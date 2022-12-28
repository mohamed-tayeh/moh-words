package com.mohamedtayeh.wosbot.db.newWord;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NewWordController {

  private NewWordRepository newWordRepository;

  public void addWord(String word) {
    newWordRepository.save(new NewWord(word));
  }
}
