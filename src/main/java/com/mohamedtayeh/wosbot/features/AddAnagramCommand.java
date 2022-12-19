package com.mohamedtayeh.wosbot.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.mohamedtayeh.wosbot.db.SubAnagram.Exceptions.InvalidSubAnagram;
import com.mohamedtayeh.wosbot.db.SubAnagram.SubAnagramController;
import com.mohamedtayeh.wosbot.features.constants.Constants;
import com.mohamedtayeh.wosbot.features.constants.Responses;
import com.mohamedtayeh.wosbot.features.dictionaryApi.DictionaryApi;
import com.mohamedtayeh.wosbot.features.messageHelper.MessageHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@NonNull
@RequiredArgsConstructor
public class AddAnagramCommand extends Command {
    private static final HashSet<String> cmdSet = new HashSet<>(List.of("!addanagram", "!adda"));
    private final MessageHelper messageHelper;
    private final SubAnagramController subAnagramController;
    private final DictionaryApi dictionaryApi;

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

        if (msgSplit.length < 3) {
            this.say(event, String.format(Responses.ADD_ANAGRAM_HELP, event.getUser().getName()));
            return;
        }

        String word = msgSplit[1];
        String subAnagram = msgSplit[2];

        if (subAnagram.length() > word.length()) {
            this.say(event, String.format(Responses.SUB_ANAGRAM_TOO_LONG, event.getUser().getName()));
            return;
        }

        if (subAnagram.length() == word.length()) {
            this.say(event, Responses.NOT_ANAGRAM_IS_WORD);
            return;
        }

        if (!subAnagramController.containsWord(word)) { // add both word and subAnagram as words
            this.say(event, String.format(Responses.WORD_NOT_FOUND, word, event.getUser().getName()));
            return;
        }
        
        if (subAnagramController.containsSubAnagram(word, subAnagram)) {
            this.say(event, String.format(Responses.ANAGRAM_ALREADY_EXISTS, event.getUser().getName()));
            return;
        }

        dictionaryApi
                .isWord(subAnagram)
                .thenAccept(isWord -> {
                    if (!isWord) {
                        this.say(event, String.format(Responses.NOT_A_WORD, subAnagram, event.getUser().getName()));
                        return;
                    }

                    try {
                        subAnagramController.addSubAnagram(word, subAnagram);
                    } catch (InvalidSubAnagram e) {
                        this.say(event, String.format(Responses.ANAGRAM_NOT_VALID, event.getUser().getName()));
                        return;
                    }

                    this.say(event, String.format(Responses.ANAGRAM_ADDED, event.getUser().getName()));
                });

    }
}
