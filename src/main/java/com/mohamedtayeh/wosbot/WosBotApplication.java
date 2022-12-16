package com.mohamedtayeh.wosbot;

import com.mohamedtayeh.wosbot.scripts.RunScript;


public class WosBotApplication {

    public static void main(String[] args) {
        Bot bot = new Bot();
        bot.registerFeatures();
        bot.start();
        System.out.println("Bot is connected...");
    }

    public static void runScript() {
        RunScript runScript = new RunScript();
        runScript.run();
    }

}
