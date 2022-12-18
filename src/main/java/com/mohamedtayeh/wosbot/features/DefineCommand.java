package com.mohamedtayeh.wosbot.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.mohamedtayeh.wosbot.features.constants.Constants;
import com.mohamedtayeh.wosbot.features.constants.Responses;
import com.mohamedtayeh.wosbot.features.dictionaryApi.DictionaryApi;
import com.mohamedtayeh.wosbot.features.messageHelper.MessageHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;

@Service
@NonNull
@RequiredArgsConstructor
public class DefineCommand extends Command {
    private final HashSet<String> cmdSet = new HashSet<>(Arrays.asList("!define"));
    private final MessageHelper messageHelper;
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

        if (cmdSet.contains(msgSplit[0]) && msgSplit.length > 1) {
            String word = msgSplit[1];
            dictionaryApi
                    .getDefinition(word)
                    .thenApply(definitions -> getResponse(word, definitions))
                    .thenAccept(res -> this.say(event, res));
        }
    }

    /**
     * Handles the response of the definition API query
     *
     * @param word        to query
     * @param definitions retrieved from the API
     * @return bot response to user
     */
    private String getResponse(String word, String definitions) {
        if (definitions.isEmpty()) {
            return String.format(Responses.NO_DEFINITION_RES, word);
        }

        return String.format(Responses.DEFINITION_RES, word, definitions);
    }
}
