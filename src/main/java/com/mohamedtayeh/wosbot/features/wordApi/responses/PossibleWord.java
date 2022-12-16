package com.mohamedtayeh.wosbot.features.wordApi.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PossibleWord implements Comparable<PossibleWord> {
    private String word;

    @Override
    public int compareTo(PossibleWord other) {
        return word.compareTo(other.word);
    }
}
