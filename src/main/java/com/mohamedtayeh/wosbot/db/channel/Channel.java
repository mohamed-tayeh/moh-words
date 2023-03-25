package com.mohamedtayeh.wosbot.db.channel;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
public class Channel {

  @Id
  private String id;
  private String channelName;
}
