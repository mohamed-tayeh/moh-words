package com.mohamedtayeh.wosbot.db.SubAnagram;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.Map;
import java.util.TreeSet;

@Document
@Data
@NonNull
@AllArgsConstructor
public class SubAnagram {
    @Id
    private String id;
    private Map<Integer, TreeSet<String>> value;

    public void addSubAnagram(String newWord) {
        int length = newWord.length();

        if (value.containsKey(length)) {
            value.get(length).add(newWord);
            return;
        }

        value.put(length, new TreeSet<>(Collections.singletonList(newWord)));
    }

    public Boolean containsWord(String word) {
        return value.get(word.length()).contains(word);
    }
}
