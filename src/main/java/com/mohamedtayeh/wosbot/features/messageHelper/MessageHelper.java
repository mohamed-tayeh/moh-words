package com.mohamedtayeh.wosbot.features.messageHelper;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.springframework.stereotype.Service;

@Service
public class MessageHelper {

  /**
   * Parse the message based on space
   *
   * @param event the event that triggered the command
   * @return array of words in the message
   */
  public String[] parseMesssage(ChannelMessageEvent event) {
    String msg = event.getMessage().trim();
    String[] msgSplit = msg.split(" ");

    for (int i = 1; i < msgSplit.length; i++) {
      msgSplit[i] = msgSplit[i].toLowerCase();
    }

    return msgSplit;
  }
}
