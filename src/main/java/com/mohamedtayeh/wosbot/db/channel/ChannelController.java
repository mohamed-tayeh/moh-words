package com.mohamedtayeh.wosbot.db.channel;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NonNull
public class ChannelController {

  private ChannelRepository channelRepository;

  public List<String> getChannels() {
    return channelRepository.findAll().stream().map(Channel::getChannelName).toList();
  }

  public void addChannel(String id, String channelName) {
    channelRepository.save(new Channel(id, channelName));
  }

  public void removeChannel(String id) {
    channelRepository.deleteById(id);
  }

  public Boolean isJoined(String id) {
    return channelRepository.findById(id).isPresent();
  }
}
