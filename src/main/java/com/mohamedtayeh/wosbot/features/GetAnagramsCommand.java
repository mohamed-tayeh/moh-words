package com.mohamedtayeh.wosbot.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.mohamedtayeh.wosbot.features.anagramFile.AnagramFile;
import com.mohamedtayeh.wosbot.features.constants.Constants;
import com.mohamedtayeh.wosbot.features.constants.Responses;
import com.mohamedtayeh.wosbot.features.messageHelper.MessageHelper;

import java.util.*;

public class GetAnagramsCommand extends Command {
    private static final List<String> cmds = Arrays.asList("!anagram", "!anagrams");
    private static final HashSet<String> cmdSet = new HashSet<String>(cmds);
    private final AnagramFile anagramFile;
    private final MessageHelper messageHelper;


    /**
     * Register events of this class with the EventManager/EventHandler
     *
     * @param eventHandler SimpleEventHandler
     */
    public GetAnagramsCommand(SimpleEventHandler eventHandler, AnagramFile anagramFile, MessageHelper messageHelper) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
        this.anagramFile = anagramFile;
        this.messageHelper = messageHelper;
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

        if (msgSplit[1].length() > Constants.MAX_WORD_LENGTH) {
            this.say(event, String.format(Responses.WORD_TOO_LONG, event.getUser().getName()));
            return;
        }

        String anagrams = anagramFile.getAnagramsString(msgSplit[1]);
        String res;

        if (anagrams.isEmpty()) {
            res = String.format(Responses.NO_ANAGRAMS_RES, msgSplit[1]);
            this.say(event, res);
            return;
        }

        res = String.format(Responses.ANAGRAMS_RES, msgSplit[1], anagrams);
        this.say(event, res);
    }
}
