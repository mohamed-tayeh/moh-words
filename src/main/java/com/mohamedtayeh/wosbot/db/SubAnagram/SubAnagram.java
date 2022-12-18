package com.mohamedtayeh.wosbot.db.SubAnagram;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
}