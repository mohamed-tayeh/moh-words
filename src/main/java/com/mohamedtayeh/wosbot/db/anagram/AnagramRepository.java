package com.mohamedtayeh.wosbot.db.anagram;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnagramRepository extends MongoRepository<Anagram, String> {

}
