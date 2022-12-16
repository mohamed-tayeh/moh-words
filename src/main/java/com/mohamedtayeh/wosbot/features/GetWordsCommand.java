package com.mohamedtayeh.wosbot.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.mohamedtayeh.wosbot.features.constants.Constants;
import com.mohamedtayeh.wosbot.features.constants.Responses;
import com.mohamedtayeh.wosbot.features.messageHelper.MessageHelper;
import com.mohamedtayeh.wosbot.features.subAnagramFile.SubAnagramFile;
import com.mohamedtayeh.wosbot.features.wordApi.WordApi;

import java.util.Arrays;
import java.util.HashSet;

public class GetWordsCommand extends Command {
    private static final HashSet<String> cmdSet = new HashSet<>(Arrays.asList("!word", "!words"));

    private final MessageHelper messageHelper;
    private final WordApi wordApi;
    private final SubAnagramFile subAnagramFile;

    public GetWordsCommand(SimpleEventHandler eventHandler, WordApi wordApi, MessageHelper messageHelper, SubAnagramFile subAnagramFile) {
        this.messageHelper = messageHelper;
        this.wordApi = wordApi;
        this.subAnagramFile = subAnagramFile;
        eventHandler.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
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

        if (!cmdSet.contains(msgSplit[0]) || msgSplit.length < 2) {
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

        this.say(event, getSubAnagrams(word, minLength, maxLength));
    }


    /**
     * Get the all the subAnagrams from the given letters
     *
     * @param letters   used to make the subAnagrams
     * @param minLength min length
     * @param maxLength max length
     * @return A string of subAnagrams sorted in descending order and alphabetical order second
     */
    private String getSubAnagrams(String letters, Integer minLength, Integer maxLength) {

        if (minLength == null) {
            minLength = Constants.MIN_WORD_LENGTH;
        }

        if (maxLength == null) {
            maxLength = Constants.MAX_WORD_LENGTH;
        }

        try {
            String subAnagrams = subAnagramFile.getAnagramsString(letters, minLength, maxLength);

            if (subAnagrams.isEmpty()) {
                return String.format(Responses.SUB_NO_ANAGRAMS_RES, letters);
            }

            return String.format(Responses.SUB_ANAGRAMS_RES, letters, subAnagrams);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
            return Responses.UNKNOWN_ERROR;
        }
    }
}
