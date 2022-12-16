package com.mohamedtayeh.wosbot;

import com.mohamedtayeh.wosbot.scripts.RunScript;

/**
 * Tasks:
 * - handling many ?
 * <p>
 * - plan:
 * <p>
 * pre-processing to finish the subAnagrams file and don't query the api
 * get all the possible hashes - done
 * for each hash get the possible words
 * these are maps with key = length and value = treeset of words
 * so have many of these maps
 * result: list of maps with key = length and value = treeset of words
 * how to return the word in sorted order
 * for each map in the maps
 * loop by length of letters to 4
 * merge the current maps into the string by sorted order
 * <p>
 * how to optimize the performance
 *
 * <p>
 * - use dependency injection
 * - checking that a class is thread safe
 * - add google styling
 * - notNull annotation
 * <p>
 * - refactoring getwords command:
 * - one function responsible for sending user messages
 * - one function responsible for parsing user messages
 * - remove the functions from subAnagramFile to anagramHelper
 */
public class WosBotApplication {

    public static void main(String[] args) {
        runScript();
        // Bot bot = new Bot();
        // bot.registerFeatures();
        // bot.start();
        // System.out.println("Bot is connected...");
    }

    public static void runScript() {
        RunScript runScript = new RunScript();
        runScript.run();
    }

}
