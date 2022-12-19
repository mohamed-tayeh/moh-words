package com.mohamedtayeh.wosbot.db.Anagram;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.TreeSet;

@Document
@Data
@NonNull
@AllArgsConstructor
public class Anagram {
    @Id
    private String id;
    private TreeSet<String> value;

    public void addValue(String newValue) {
        value.add(newValue);
    }

    public Boolean containsWord(String word) {
        return value.contains(word);
    }
}
