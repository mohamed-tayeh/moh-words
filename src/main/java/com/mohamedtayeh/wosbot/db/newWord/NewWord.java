package com.mohamedtayeh.wosbot.db.newWord;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
public class NewWord {

  @Id
  private String id;
}
