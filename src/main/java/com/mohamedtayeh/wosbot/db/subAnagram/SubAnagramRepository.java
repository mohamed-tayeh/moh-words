package com.mohamedtayeh.wosbot.db.subAnagram;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubAnagramRepository extends MongoRepository<SubAnagram, String> {

}
