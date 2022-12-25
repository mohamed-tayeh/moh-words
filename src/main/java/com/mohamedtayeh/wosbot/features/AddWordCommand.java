package com.mohamedtayeh.wosbot.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.mohamedtayeh.wosbot.db.SubAnagram.SubAnagramController;
import com.mohamedtayeh.wosbot.features.constants.Constants;
import com.mohamedtayeh.wosbot.features.constants.Responses;
import com.mohamedtayeh.wosbot.features.dictionaryApi.DictionaryApi;
import com.mohamedtayeh.wosbot.features.messageHelper.MessageHelper;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddWordCommand extends Command {

  private static final HashSet<String> cmdSet = new HashSet<String>(List.of("!addw", "!addword"));
  private final MessageHelper messageHelper;
  private final DictionaryApi dictionaryApi;
  private final SubAnagramController subAnagramController;


  /**
   * Used to add a listener to the event handler
   *
   * @param event the event handler to listen to
   */
  @Override
  public void handleEvent(SimpleEventHandler event) {
    event.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
  }

  /**
   * Subscribe to the ChannelMessage Event and write the output to the console
   */
  @Override
  public void onChannelMessage(ChannelMessageEvent event) {

    if (!event.getMessage().startsWith(Constants.COMMAND_PREFIX)) {
      return;
    }

    String[] msgSplit = messageHelper.parseMesssage(event);

    if (!cmdSet.contains(msgSplit[0]) || msgSplit.length < 2) {
      return;
    }

    String word = msgSplit[1].toLowerCase();

    if (subAnagramController.containsWord(word)) {
      this.say(event, String.format(Responses.WORD_EXISTS, event.getUser().getName()));
      return;
    }

    dictionaryApi
        .isWord(word)
        .thenAccept(isWord -> {
          if (isWord) {
            this.say(event, String.format(Responses.WORD_ADDED, event.getUser().getName()));
            subAnagramController.addWord(word);
            return;
          }

          this.say(event, String.format(Responses.NOT_A_WORD, word, event.getUser().getName()));
        });

  }

}
