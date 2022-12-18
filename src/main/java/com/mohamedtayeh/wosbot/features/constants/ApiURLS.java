package com.mohamedtayeh.wosbot.features.constants;

import org.springframework.stereotype.Component;

@Component
public class ApiURLS {
    public static final String DICTIONARY_API = "https://api.dictionaryapi.dev/api/v2/entries/en/%s";
    public static final String WORD_API = "https://fly.wordfinderapi.com/api/search?letters=%s&word_sorting=points&group_by_length=true&page_size=20&dictionary=all_en&longer_than=%s&shorter_than=%s";
}
