package com.mohamedtayeh.wosbot.features.dictionaryApi.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Definition {
    private String definition = "";
}