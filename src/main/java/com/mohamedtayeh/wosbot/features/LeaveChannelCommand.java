package com.mohamedtayeh.wosbot.features;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.mohamedtayeh.wosbot.Bot;
import com.mohamedtayeh.wosbot.db.channel.ChannelController;
import com.mohamedtayeh.wosbot.features.constants.Constants;
import com.mohamedtayeh.wosbot.features.constants.Responses;
import com.mohamedtayeh.wosbot.features.messageHelper.MessageHelper;
import java.util.HashSet;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
@NonNull
@RequiredArgsConstructor
public class LeaveChannelCommand extends Command {

  private final HashSet<String> cmdSet = new HashSet<>(List.of("!leave"));
  private final MessageHelper messageHelper;
  private final ChannelController channelController;
  @Setter
  private Bot bot;

  /**
   * Used to add a listener to the event handler
   *
   * @param event the event handler to listen to
   */
  @Override
  public void handleEvent(SimpleEventHandler event) {
    event.onEvent(ChannelMessageEvent.class, this::onChannelMessage);
  }

  /**
   * Subscribe to the ChannelMessage Event and handles adding anagram commands
   *
   * @param event the event to handle
   */
  @Override
  public void onChannelMessage(ChannelMessageEvent event) {

    if (!event.getMessage().startsWith(Constants.COMMAND_PREFIX)) {
      return;
    }

    String cmd = messageHelper.parseMesssage(event)[0];
    if (cmdSet.contains(cmd) && event.getChannel().getName()
        .equalsIgnoreCase(Constants.HOST_CHANNEL)) {

      String channelId = event.getUser().getId();
      String channelName = event.getUser().getName();

      if (!channelController.isJoined(channelId)) {
        this.say(event, Responses.ALREADY_LEFT_CHANNEL);
        return;
      }

      channelController.removeChannel(channelId);
      bot.leaveChannel(channelName);
      this.say(event, String.format(Responses.LEAVE_CHANNEL, channelName));
    }
  }
}
