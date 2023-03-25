package com.mohamedtayeh.wosbot.features.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.mohamedtayeh.wosbot.db.anagram.AnagramController;
import com.mohamedtayeh.wosbot.features.messageHelper.MessageUtils;
import com.mohamedtayeh.wosbot.features.utils.Constants;
import com.mohamedtayeh.wosbot.features.utils.Responses;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAnagramsCommand extends Command {

  private static final List<String> cmds = Arrays.asList("!anagram", "!anagrams");
  private static final HashSet<String> cmdSet = new HashSet<String>(cmds);
  private final AnagramController anagramController;

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
   * Subscribe to the ChannelMessage Event and handles adding anagram commands
   *
   * @param event the event to handle
   */
  @Override
  public void onChannelMessage(ChannelMessageEvent event) {
    if (!event.getMessage().startsWith(Constants.COMMAND_PREFIX)) {
      return;
    }

    String[] msgSplit = MessageUtils.parseMesssage(event);

    if (!cmdSet.contains(msgSplit[0]) || msgSplit.length < 2) {
      return;
    }

    String word = msgSplit[1];
    if (msgSplit[1].length() > Constants.MAX_WORD_LENGTH) {
      this.say(event, String.format(Responses.WORD_TOO_LONG, event.getUser().getName()));
      return;
    }

    String anagrams = anagramController.getAnagramsString(word);
    String res;

    if (anagrams.isEmpty()) {
      res = String.format(Responses.NO_ANAGRAMS_RES, word);
      this.say(event, res);
      return;
    }

    res = String.format(Responses.ANAGRAMS_RES, word, anagrams);
    this.say(event, res);
  }


}
