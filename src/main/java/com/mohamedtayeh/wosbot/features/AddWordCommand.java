package com.mohamedtayeh.wosbot.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.mohamedtayeh.wosbot.features.constants.Constants;
import com.mohamedtayeh.wosbot.features.constants.Responses;
import com.mohamedtayeh.wosbot.features.dictionaryApi.DictionaryApi;
import com.mohamedtayeh.wosbot.features.messageHelper.MessageHelper;
import com.mohamedtayeh.wosbot.features.subAnagramFile.SubAnagramFile;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@NonNull
@RequiredArgsConstructor
public class AddWordCommand extends Command {
    private static final HashSet<String> cmdSet = new HashSet<String>(List.of("!addw", "!addword"));
    private final MessageHelper messageHelper;
    private final SubAnagramFile subAnagramFile;
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

        String word = msgSplit[1].toLowerCase();

        if (subAnagramFile.containsWord(word)) {
            this.say(event, String.format(Responses.WORD_EXISTS, event.getUser().getName()));
            return;
        }

        dictionaryApi
                .isWord(word)
                .thenAccept(isWord -> {
                    if (isWord) {
                        subAnagramFile.addWord(word);
                        this.say(event, String.format(Responses.WORD_ADDED, event.getUser().getName()));
                        return;
                    }

                    this.say(event, String.format(Responses.NOT_A_WORD, word, event.getUser().getName()));
                });

    }

}
