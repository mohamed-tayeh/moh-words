package com.mohamedtayeh.wosbot.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.mohamedtayeh.wosbot.features.constants.Responses;
import com.mohamedtayeh.wosbot.features.dictionaryApi.DictionaryApi;
import com.mohamedtayeh.wosbot.features.messageHelper.MessageHelper;
import com.mohamedtayeh.wosbot.features.subAnagramFile.SubAnagramFile;

import java.util.HashSet;
import java.util.List;

public class AddWordCommand extends Command {
    private static final HashSet<String> cmdSet = new HashSet<String>(List.of("!addw", "!addword"));
    private final MessageHelper messageHelper;
    private final SubAnagramFile subAnagramFile;
    private final DictionaryApi dictionaryApi;


    /**
     * Register events of this class with the EventManager/EventHandler
     *
     * @param eventHandler SimpleEventHandler
     */
    public AddWordCommand(SimpleEventHandler eventHandler, SubAnagramFile subAnagramFile, MessageHelper messageHelper, DictionaryApi dictionaryApi) {
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

        String[] msgSplit = messageHelper.parseMesssage(event);

        if (!cmdSet.contains(msgSplit[0]) || msgSplit.length < 2) {
            return;
        }

        String word = msgSplit[1].toLowerCase();

        if (subAnagramFile.containsWord(word)) {
            this.say(event, String.format(Responses.WORD_EXISTS, event.getUser().getName()));
            return;
        }

        try {

            if (!dictionaryApi.isWord(word)) {
                this.say(event, String.format(Responses.NOT_A_WORD, word, event.getUser().getName()));
                return;
            }

        } catch (Exception e) {
            this.say(event, Responses.UNKNOWN_ERROR);
            return;
        }

        subAnagramFile.addWord(word);

        this.say(event, String.format(Responses.WORD_ADDED, event.getUser().getName()));

    }

}
