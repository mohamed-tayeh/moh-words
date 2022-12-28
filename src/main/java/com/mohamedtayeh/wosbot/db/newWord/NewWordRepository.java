package com.mohamedtayeh.wosbot.db.newWord;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewWordRepository extends MongoRepository<NewWord, String> {

}
