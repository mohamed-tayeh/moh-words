package com.mohamedtayeh.wosbot.features.messageHelper;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class MessageHelper {
    public String[] parseMesssage(ChannelMessageEvent event) {
        String msg = event.getMessage().trim();
        String[] msgSplit = msg.split(" ");
        msgSplit[0] = msgSplit[0].toLowerCase();
        return msgSplit;
    }

    public String[] parseMessageWithComma(ChannelMessageEvent event) {
        String msg = event.getMessage().trim();
        String[] msgSplit = msg.split(" ");
        msgSplit[0] = msgSplit[0].toLowerCase();

        return msgSplit;
    }
}
