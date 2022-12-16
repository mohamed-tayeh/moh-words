package com.mohamedtayeh.wosbot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.mohamedtayeh.wosbot.features.*;
import com.mohamedtayeh.wosbot.features.anagramFile.AnagramFile;
import com.mohamedtayeh.wosbot.features.anagramHelper.AnagramHelper;
import com.mohamedtayeh.wosbot.features.constants.FilePaths;
import com.mohamedtayeh.wosbot.features.dictionaryApi.DictionaryApi;
import com.mohamedtayeh.wosbot.features.messageHelper.MessageHelper;
import com.mohamedtayeh.wosbot.features.subAnagramFile.SubAnagramFile;
import com.mohamedtayeh.wosbot.features.wordApi.WordApi;

import java.io.File;
import java.util.List;

public class Bot {
    /**
     * Twitch4J API
     */
    private final ITwitchClient twitchClient;
    /**
     * Holds the Bot Configuration
     */
    private Configuration configuration;

    /**
     * Constructor
     */
    public Bot() {
        // Load Configuration
        loadConfiguration();

        TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();

        // region Auth
        OAuth2Credential credential = new OAuth2Credential(
                "twitch",
                configuration.getCredentials().get("irc"));
        // endregion

        // region TwitchClient
        twitchClient = clientBuilder
                .withClientId(configuration.getApi().get("twitch_client_id"))
                .withClientSecret(configuration.getApi().get("twitch_client_secret"))
                .withEnableHelix(true)
                /*
                 * Chat Module
                 * Joins irc and triggers all chat based events (viewer
                 * join/leave/sub/bits/gifted subs/...)
                 */
                .withChatAccount(credential)
                .withEnableChat(true)
                /*
                 * Build the TwitchClient Instance
                 */
                .build();
        // endregion
    }

    /**
     * Method to register all features
     */
    public void registerFeatures() {
        SimpleEventHandler eventHandler = twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class);

        ObjectMapper objectMapper = new ObjectMapper();
        AnagramHelper anagramHelper = new AnagramHelper();
        AnagramFile anagramFile = new AnagramFile(objectMapper, anagramHelper);
        SubAnagramFile subAnagramFile = new SubAnagramFile(objectMapper, anagramHelper, anagramFile);

        WordApi wordApi = new WordApi(objectMapper);
        DictionaryApi dictionaryApi = new DictionaryApi(objectMapper);

        MessageHelper messageHelper = new MessageHelper();

        new GetWordsCommand(eventHandler, wordApi, messageHelper, subAnagramFile);
        new GetAnagramsCommand(eventHandler, anagramFile, messageHelper);

        new DefineCommand(eventHandler, dictionaryApi, messageHelper);

        new AddWordCommand(eventHandler, subAnagramFile, messageHelper, dictionaryApi);
        new AddAnagramCommand(eventHandler, subAnagramFile, messageHelper, dictionaryApi);
    }

    /**
     * Load the Configuration
     */
    private void loadConfiguration() {
        try {
            // Credentials
            ObjectMapper mapper = new ObjectMapper();
            configuration = mapper.readValue(new File(FilePaths.CONFIG_FILE_NAME), Configuration.class);
            // Channels
            List<String> channels = mapper.readValue(new File(FilePaths.CHANNELS_FILE_NAME), new TypeReference<List<String>>() {
            });
            configuration.setChannels(channels);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Unable to load Configuration ... Exiting.");
            System.exit(1);
        }
    }

    public void start() {
        for (String channel : configuration.getChannels()) {
            twitchClient.getChat().joinChannel(channel);
        }
    }
}
