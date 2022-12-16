package com.mohamedtayeh.wosbot.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.mohamedtayeh.wosbot.features.constants.Constants;
import com.mohamedtayeh.wosbot.features.constants.Responses;
import com.mohamedtayeh.wosbot.features.dictionaryApi.DictionaryApi;
import com.mohamedtayeh.wosbot.features.messageHelper.MessageHelper;
import com.mohamedtayeh.wosbot.features.subAnagramFile.Exceptions.InvalidSubAnagram;
import com.mohamedtayeh.wosbot.features.subAnagramFile.SubAnagramFile;

import java.util.HashSet;
import java.util.List;

public class AddAnagramCommand extends Command {
    private static final HashSet<String> cmdSet = new HashSet<>(List.of("!addanagram", "!adda"));
    private final MessageHelper messageHelper;
    private final SubAnagramFile subAnagramFile;
    private final DictionaryApi dictionaryApi;


    /**
     * Register events of this class with the EventManager/EventHandler
     *
     * @param eventHandler SimpleEventHandler
     */
    public AddAnagramCommand(SimpleEventHandler eventHandler, SubAnagramFile subAnagramFile, MessageHelper messageHelper, DictionaryApi dictionaryApi) {
        this.subAnagramFile = subAnagramFile;
        this.messageHelper = messageHelper;
        this.dictionaryApi = dictionaryApi;

        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
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

        if (msgSplit.length == 2) {
            this.say(event, String.format(Responses.ADD_ANAGRAM_HELP, event.getUser().getName()));
            return;
        }

        String word = msgSplit[1].toLowerCase();
        String subAnagram = msgSplit[2].toLowerCase();

        if (subAnagram.length() > word.length()) {
            this.say(event, String.format(Responses.SUB_ANAGRAM_TOO_LONG, event.getUser().getName()));
            return;
        }

        if (subAnagram.length() == word.length()) {
            this.say(event, Responses.NOT_ANAGRAM_IS_WORD);
            return;
        }

        if (!subAnagramFile.containsWord(word)) {
            this.say(event, String.format(Responses.WORD_NOT_FOUND, event.getUser().getName(), word));
            return;
        }

        if (subAnagramFile.containsSubAnagram(word, subAnagram)) {
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
                        subAnagramFile.addSubAnagram(word, subAnagram);
                    } catch (InvalidSubAnagram e) {
                        this.say(event, String.format(Responses.ANAGRAM_NOT_VALID, event.getUser().getName()));
                        return;
                    }

                    this.say(event, String.format(Responses.ANAGRAM_ADDED, event.getUser().getName()));
                });

    }

}
