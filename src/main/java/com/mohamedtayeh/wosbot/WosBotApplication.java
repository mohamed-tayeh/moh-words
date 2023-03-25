package com.mohamedtayeh.wosbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@Slf4j
public class WosBotApplication {

  public static void main(String[] args) {
    log.info("Bot is starting...");
    ApplicationContext context = SpringApplication.run(WosBotApplication.class, args);
    Bot bot = context.getBean(Bot.class);
    bot.start();
    log.info("Bot is connected...");
  }
}
