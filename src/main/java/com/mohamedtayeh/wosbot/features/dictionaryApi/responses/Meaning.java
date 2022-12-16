package com.mohamedtayeh.wosbot.features.dictionaryApi.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Meaning {
    String partOfSpeech = "";
    Definition[] definitions = {};
    String[] synonyms = {};
    String[] antonyms = {};
}
