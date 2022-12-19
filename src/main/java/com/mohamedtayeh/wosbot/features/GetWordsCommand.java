package com.mohamedtayeh.wosbot.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.mohamedtayeh.wosbot.db.SubAnagram.SubAnagramController;
import com.mohamedtayeh.wosbot.features.constants.Constants;
import com.mohamedtayeh.wosbot.features.constants.Responses;
import com.mohamedtayeh.wosbot.features.messageHelper.MessageHelper;
import com.mohamedtayeh.wosbot.features.wordApi.WordApi;
import com.mohamedtayeh.wosbot.features.wordApi.responses.AnagramRes;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@NonNull
@RequiredArgsConstructor
public class GetWordsCommand extends Command {
    private final Set<String> cmdApiSet = new HashSet<>(Arrays.asList("!wordapi", "!wordsapi"));
    private final Set<String> cmdDictSet = new HashSet<>(Arrays.asList("!word", "!words"));
    private final Set<String> cmdSet = new HashSet<>();

    private final MessageHelper messageHelper;
    private final WordApi wordApi;
    private final SubAnagramController subAnagramController;

    @PostConstruct
    public void init() {
        cmdSet.addAll(cmdApiSet);
        cmdSet.addAll(cmdDictSet);
    }

    @Override
    public void handleEvent(SimpleEventHandler event) {
        event.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    /**
     * Respond to the !word or !words command by replying with all possible sub-anagrams
     */
    @Override
    public void onChannelMessage(ChannelMessageEvent event) {

        if (!event.getMessage().startsWith(Constants.COMMAND_PREFIX)) {
            return;
        }

        String[] msgSplit = messageHelper.parseMesssage(event);
        String cmd = msgSplit[0];

        if (!cmdSet.contains(cmd) || msgSplit.length < 2) {
            return;
        }

        String word = msgSplit[1];

        if (word.length() > Constants.MAX_WORD_LENGTH) {
            this.say(event, String.format(Responses.WORD_TOO_LONG, event.getUser().getName()));
            return;
        }

        Integer minLength = null;
        Integer maxLength = null;

        if (msgSplit.length > 2) {
            try {
                minLength = Integer.parseInt(msgSplit[2]);
            } catch (NumberFormatException e) {
                this.say(event, String.format(Responses.INVALID_LENGTH_PARAM, event.getUser().getName()));
                return;
            }
        }

        if (msgSplit.length > 3) {
            try {
                maxLength = Integer.parseInt(msgSplit[3]);
            } catch (NumberFormatException e) {
                this.say(event, String.format(Responses.INVALID_LENGTH_PARAM, event.getUser().getName()));
                return;
            }

            if (maxLength < minLength) {
                this.say(event, String.format(Responses.INVALID_LENGTH_ORDER, event.getUser().getName()));
                return;
            }
        }

        if (minLength != null && minLength > word.length()) {
            this.say(event, String.format(Responses.INVALID_MIN_LENGTH, event.getUser().getName()));
            return;
        }

        if (minLength == null) {
            minLength = Constants.MIN_WORD_LENGTH;
        }

        if (maxLength == null) {
            maxLength = Constants.MAX_WORD_LENGTH;
        }

        CompletableFuture<String> completable;
        if (cmdDictSet.contains(cmd)) {
            completable = commandResByDB(event, word, minLength, maxLength);
        } else {
            completable = commandResByApi(event, word, minLength, maxLength);
        }

        completable
                .thenApply(res -> handleRes(res, word))
                .thenAccept(res -> this.say(event, res));
    }

    private CompletableFuture<String> commandResByDB(ChannelMessageEvent event, String letters, Integer minLength, Integer maxLength) {
        return subAnagramController.getSubAnagramsString(letters, minLength, maxLength);
    }

    private CompletableFuture<String> commandResByApi(ChannelMessageEvent event, String letters, Integer minLength, Integer maxLength) {
        return wordApi.getWords(letters, minLength, maxLength)
                .thenApply(AnagramRes::getAnagramsString);
    }

    private String handleRes(String subAnagrams, String word) {
        if (subAnagrams.isEmpty()) {
            return String.format(Responses.SUB_NO_ANAGRAMS_RES, word);
        }

        return String.format(Responses.SUB_ANAGRAMS_RES, word, subAnagrams);
    }

}
