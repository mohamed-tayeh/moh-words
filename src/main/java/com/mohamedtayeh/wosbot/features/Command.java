package com.mohamedtayeh.wosbot.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class Command {

    public abstract void onChannelMessage(ChannelMessageEvent event);

    public abstract void handleEvent(SimpleEventHandler event);

    /**
     * Sends a message to the chat
     *
     * @param event the event that triggered the command
     * @param msg   the message to send
     */
    public void say(ChannelMessageEvent event, String msg) {

        List<String> messages = splitOnSpaces(msg, 500);
        String channelName = event.getChannel().getName();
        TwitchChat twitchChat = event.getTwitchChat();

        for (String m : messages) {
            twitchChat.sendMessage(channelName, m);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Error while sleeping to send message");
            }
        }
    }

    /**
     * Splits a string on spaces
     *
     * @param msg          the string to split
     * @param maxMsgLength the size of each chunk
     * @return a list of strings
     */
    private List<String> splitOnSpaces(String msg, int maxMsgLength) {

        if (msg.length() <= maxMsgLength) {
            return new ArrayList<>(List.of(msg));
        }

        ArrayList<String> res = new ArrayList<>();

        int startIndex = 0;
        int endIndex = maxMsgLength;
        int N = msg.length();

        while (startIndex < N - 1) {
            int spaceIndex = msg.lastIndexOf(' ', endIndex);

            if (spaceIndex == -1 || spaceIndex <= startIndex || N - startIndex + 1 <= maxMsgLength) {
                spaceIndex = Math.min(startIndex + maxMsgLength, N);
            }

            String msgSubString = msg.substring(startIndex, spaceIndex).trim();

            if (msgSubString.length() > 0) {
                res.add(msgSubString);
            }


            startIndex = spaceIndex; // to skip the space

            if (spaceIndex < N) {
                startIndex += (msg.charAt(spaceIndex) == ' ' ? 1 : 0);
            }

            endIndex = startIndex + maxMsgLength;

        }

        return res;
    }

}
