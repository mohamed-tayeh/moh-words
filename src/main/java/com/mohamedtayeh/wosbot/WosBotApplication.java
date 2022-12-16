package com.mohamedtayeh.wosbot;

import com.mohamedtayeh.wosbot.scripts.RunScript;

/**
 * - refactoring:
 * - remove the functions from subAnagramFile to anagramHelper
 * - making the dictionary api call non-blocking
 * <p>
 * - use dependency injection
 * - checking that a class is thread safe
 * - add google styling
 * - notNull annotation
 * <p>
 */
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
