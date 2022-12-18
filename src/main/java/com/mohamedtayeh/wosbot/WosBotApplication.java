package com.mohamedtayeh.wosbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class WosBotApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(WosBotApplication.class, args);
        Bot bot = context.getBean(Bot.class);
        bot.start();
        System.out.println("Bot is connected...");
    }
}
