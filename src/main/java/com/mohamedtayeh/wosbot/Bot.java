package com.mohamedtayeh.wosbot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.mohamedtayeh.wosbot.features.AddAnagramCommand;
import com.mohamedtayeh.wosbot.features.AddWordCommand;
import com.mohamedtayeh.wosbot.features.DefineCommand;
import com.mohamedtayeh.wosbot.features.GetAnagramsCommand;
import com.mohamedtayeh.wosbot.features.GetWordsCommand;
import com.mohamedtayeh.wosbot.features.constants.FilePaths;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
@NonNull
public class Bot {

  /**
   * Twitch4J API
   */
  private final ITwitchClient twitchClient;
  private final GetWordsCommand getWordsCommand;
  private final DefineCommand defineCommand;
  private final GetAnagramsCommand getAnagramsCommand;
  private final AddAnagramCommand addAnagramCommand;
  private final AddWordCommand addWordCommand;

  /**
   * Holds the Bot Configuration
   */
  private Configuration configuration;

  public Bot(DefineCommand defineCommand, GetAnagramsCommand getAnagramsCommand,
      GetWordsCommand getWordsCommand,
      AddAnagramCommand addAnagramCommand, AddWordCommand addWordCommand) {
    // Load Configuration
    loadConfiguration();

    TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();

    OAuth2Credential credential = new OAuth2Credential(
        "twitch",
        configuration.getCredentials().get("irc"));

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

    this.defineCommand = defineCommand;
    this.getAnagramsCommand = getAnagramsCommand;
    this.getWordsCommand = getWordsCommand;
    this.addAnagramCommand = addAnagramCommand;
    this.addWordCommand = addWordCommand;
  }

  /**
   * Method to register all features
   */
  @PostConstruct
  public void registerFeatures() {
    // Register Event Handlers
    SimpleEventHandler eventHandler = twitchClient.getEventManager()
        .getEventHandler(SimpleEventHandler.class);
    defineCommand.handleEvent(eventHandler);
    getAnagramsCommand.handleEvent(eventHandler);
    getWordsCommand.handleEvent(eventHandler);
    addAnagramCommand.handleEvent(eventHandler);
    addWordCommand.handleEvent(eventHandler);
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
      List<String> channels = mapper.readValue(new File(FilePaths.CHANNELS_FILE_NAME),
          new TypeReference<List<String>>() {
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
