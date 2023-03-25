package com.mohamedtayeh.wosbot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.ITwitchClient;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.mohamedtayeh.wosbot.db.channel.ChannelController;
import com.mohamedtayeh.wosbot.features.commands.Command;
import com.mohamedtayeh.wosbot.features.commands.CommandReflectingOnBot;
import com.mohamedtayeh.wosbot.features.utils.FilePaths;
import com.mohamedtayeh.wosbot.features.utils.GeneralUtils;
import io.github.bucket4j.Bandwidth;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Bot {

  private final ITwitchClient twitchClient;
  private final Configuration configuration;
  private final ChannelController channelController;
  private final ApplicationContext applicationContext;

  public Bot(ChannelController channelController, ApplicationContext applicationContext) {
    this.configuration = loadConfiguration();
    this.twitchClient = createTwitchClient();
    this.channelController = channelController;
    this.applicationContext = applicationContext;
    this.registerFeatures();
  }

  /**
   * Method to register all features
   */
  public void registerFeatures() {
    // Register Event Handlers
    SimpleEventHandler eventHandler = twitchClient.getEventManager()
        .getEventHandler(SimpleEventHandler.class);

    log.info("I am about to get the commands!");
    Collection<Command> commands = applicationContext.getBeansOfType(Command.class).values();
    log.info("{} commands are ready", commands.size());

    for (Command command : commands) {

      log.info("getting command: {}", command);

      if (command instanceof CommandReflectingOnBot commandReflectingOnBot) {
        commandReflectingOnBot.setBot(this);
      }

      command.handleEvent(eventHandler);
    }
  }

  private TwitchClient createTwitchClient() {
    TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();

    OAuth2Credential credential = new OAuth2Credential(
        "twitch",
        configuration.getCredentials().get("irc"));

    return clientBuilder
        .withClientId(configuration.getApi().get("twitch_client_id"))
        .withClientSecret(configuration.getApi().get("twitch_client_secret"))
        .withEnableHelix(true)
        .withChatAccount(credential)
        .withEnableChat(true)
        .withChatChannelMessageLimit(
            Bandwidth.simple(1, Duration.ofSeconds(2)).withId("per-channel-limit"))
        .build();
  }

  /**
   * Load the Configuration
   */
  private Configuration loadConfiguration() {
    try {
      // Credentials
      Configuration configuration = GeneralUtils.objectMapper.readValue(
          new File(FilePaths.CONFIG_FILE_NAME),
          Configuration.class);

      // Channels
      List<String> channels = GeneralUtils.objectMapper.readValue(
          new File(FilePaths.CHANNELS_FILE_NAME),
          new TypeReference<>() {
          });

      configuration.setChannels(channels);

      return configuration;
    } catch (IOException e) {
      log.error("Unable to load Configuration ... Exiting.", e);
      System.exit(1);
      throw new RuntimeException(e);
    }
  }

  /**
   * Connect to Twitch
   */
  public void start() {
    channelController.getChannels().forEach(channel -> twitchClient.getChat().joinChannel(channel));
  }

  /**
   * Joins a channel by its name
   *
   * @param channel name of the channel
   */
  public void joinChannel(String channel) {
    twitchClient.getChat().joinChannel(channel);
  }

  /**
   * Leaves a channel by its name
   *
   * @param channel name of the channel
   */
  public void leaveChannel(String channel) {
    twitchClient.getChat().leaveChannel(channel);
  }
}
