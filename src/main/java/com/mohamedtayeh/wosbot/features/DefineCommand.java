package com.mohamedtayeh.wosbot.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.mohamedtayeh.wosbot.features.constants.Responses;
import com.mohamedtayeh.wosbot.features.dictionaryApi.DictionaryApi;
import com.mohamedtayeh.wosbot.features.messageHelper.MessageHelper;

public class DefineCommand extends Command {

    private final String defineCommand = "!define";
    private final MessageHelper messageHelper;
    private DictionaryApi dictionaryApi;

    /**
     * Register events of this class with the EventManager/EventHandler
     *
     * @param eventHandler SimpleEventHandler
     */
    public DefineCommand(SimpleEventHandler eventHandler, DictionaryApi dictionaryApi, MessageHelper messageHelper) {
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
        this.dictionaryApi = dictionaryApi;
        this.messageHelper = messageHelper;
    }

    /**
     * Subscribe to the ChannelMessage Event and write the output to the console
     */
    @Override
    public void onChannelMessage(ChannelMessageEvent event) {

        String[] msgSplit = messageHelper.parseMesssage(event);

        if (msgSplit[0].equals(defineCommand) && msgSplit.length > 1) {
            String res;

            try {
                String definitions = dictionaryApi.getDefinition(msgSplit[1]);
                if (definitions.isEmpty()) {
                    res = String.format(Responses.NO_DEFINITION_RES, msgSplit[1]);
                } else {
                    res = String.format(Responses.DEFINITION_RES, msgSplit[1], definitions);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                res = Responses.UNKNOWN_ERROR;
            }

            this.say(event, res);
        }
    }

}
