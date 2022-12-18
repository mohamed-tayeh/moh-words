package com.mohamedtayeh.wosbot.db.SubAnagram;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubAnagramRepository extends MongoRepository<SubAnagram, String> {

}
