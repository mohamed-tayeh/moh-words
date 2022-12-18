package com.mohamedtayeh.wosbot.db.Anagram;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnagramRespository extends MongoRepository<Anagram, String> {

}
