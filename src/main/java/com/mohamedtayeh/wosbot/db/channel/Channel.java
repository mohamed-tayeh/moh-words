package com.mohamedtayeh.wosbot.db.channel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NonNull
@AllArgsConstructor
public class Channel {

  @Id
  private String id;
  private String channelName;
}
