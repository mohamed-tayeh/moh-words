package com.mohamedtayeh.wosbot.features.wordApi.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Arrays;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class WordPage {
    private PossibleWord[] wordList;
    @JsonProperty("length")
    private Integer wordLength;

    public PossibleWord[] getWordList() {
        return wordList;
    }

    public void setWordList(PossibleWord[] wordList) {
        this.wordList = wordList;
        Arrays.sort(this.wordList);
    }
}
