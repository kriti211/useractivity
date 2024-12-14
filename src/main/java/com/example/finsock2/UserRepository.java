package com.example.finsock2;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserActivity,String> {
}
