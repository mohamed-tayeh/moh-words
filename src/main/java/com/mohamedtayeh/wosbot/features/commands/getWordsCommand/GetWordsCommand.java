package com.mohamedtayeh.wosbot.features.commands.getWordsCommand;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.enums.CommandPermission;
import com.mohamedtayeh.wosbot.db.subAnagram.SubAnagramController;
import com.mohamedtayeh.wosbot.features.commands.Command;
import com.mohamedtayeh.wosbot.features.commands.getWordsCommand.Exceptions.InvalidUserInput;
import com.mohamedtayeh.wosbot.features.messageHelper.MessageUtils;
import com.mohamedtayeh.wosbot.features.utils.Constants;
import com.mohamedtayeh.wosbot.features.utils.Responses;
import com.mohamedtayeh.wosbot.features.wordApi.WordApi;
import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetWordsCommand extends Command {

  private final Set<String> cmdApiSet = new HashSet<>(List.of("!wordapi", "!wordsapi"));
  private final Set<String> cmdDictSet = new HashSet<>(List.of("!word", "!words"));
  private final Set<String> cmdSet = new HashSet<>();

  private final SubAnagramController subAnagramController;

  @PostConstruct
  private void init() {
    cmdSet.addAll(cmdApiSet);
    cmdSet.addAll(cmdDictSet);
  }

  /**
   * Handles the response from the api/db
   *
   * @param subAnagrams the response from the api/db
   * @param word        the word to find anagrams for
   */
  private String handleRes(String subAnagrams, String word) {
    if (subAnagrams.isEmpty()) {
      return String.format(Responses.SUB_NO_ANAGRAMS_RES, word);
    }

    return String.format(Responses.SUB_ANAGRAMS_RES, word, subAnagrams);
  }

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
   * Sanitize the input and return the word and the min and max length
   *
   * @param event    the event to handle
   * @param msgSplit the words send by the user separated by spaces
   * @return an array of the word and the min and max length
   * @throws InvalidUserInput if the user input is invalid
   */
  private Object[] sanitizeInput(ChannelMessageEvent event, String[] msgSplit)
      throws InvalidUserInput {

    Set<CommandPermission> perms = event.getPermissions();
    if (!(perms.contains(CommandPermission.MODERATOR) || perms.contains(CommandPermission.VIP)
        || perms.contains(CommandPermission.BROADCASTER))) {
      this.say(event, Responses.ONLY_MODS_AND_VIPS);
      throw new InvalidUserInput("Not VIP, Mod or broadcaster");
    }

    String word = msgSplit[1];

    if (word.length() > Constants.MAX_WORD_LENGTH) {
      this.say(event, String.format(Responses.WORD_TOO_LONG, event.getUser().getName()));
      throw new InvalidUserInput("Word too long");
    }

    Integer minLength = null;
    Integer maxLength = null;

    if (msgSplit.length > 2) {
      try {
        minLength = Integer.parseInt(msgSplit[2]);
      } catch (NumberFormatException e) {
        this.say(event, String.format(Responses.INVALID_LENGTH_PARAM, event.getUser().getName()));
        throw new InvalidUserInput("minLength is not number");
      }
    }

    if (msgSplit.length > 3) {
      try {
        maxLength = Integer.parseInt(msgSplit[3]);
      } catch (NumberFormatException e) {
        this.say(event, String.format(Responses.INVALID_LENGTH_PARAM, event.getUser().getName()));
        throw new InvalidUserInput("maxLength is not number");
      }

      if (maxLength < minLength) {
        this.say(event, String.format(Responses.INVALID_LENGTH_ORDER, event.getUser().getName()));
        throw new InvalidUserInput("maxLengh < minLength");
      }
    }

    if (minLength != null && minLength > word.length()) {
      this.say(event, String.format(Responses.INVALID_MIN_LENGTH, event.getUser().getName()));
      throw new InvalidUserInput("word.length() < minLength");
    }

    if (minLength == null) {
      minLength = Constants.MIN_WORD_LENGTH;
    }

    if (maxLength == null) {
      maxLength = Constants.MAX_WORD_LENGTH;
    }

    return new Object[]{word, minLength, maxLength};
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
    String cmd = msgSplit[0];

    if (!cmdSet.contains(cmd) || msgSplit.length < 2) {
      return;
    }
    Object[] input;

    try {
      input = sanitizeInput(event, msgSplit);
    } catch (InvalidUserInput e) {
      log.error("User error when calling get word", e);
      return;
    }

    String word = (String) input[0];
    Integer minLength = (Integer) input[1];
    Integer maxLength = (Integer) input[2];

    CompletableFuture<String> completable;
    if (cmdDictSet.contains(cmd)) {
      completable = subAnagramController.getSubAnagramsString(word, minLength, maxLength);
    } else {
      completable = WordApi.getWords(word, minLength, maxLength);
    }

    completable
        .thenApply(res -> handleRes(res, word))
        .thenAccept(res -> this.say(event, res));
  }

}
