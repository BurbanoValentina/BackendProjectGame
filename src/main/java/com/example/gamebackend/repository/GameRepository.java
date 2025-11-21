package com.example.gamebackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.gamebackend.model.Game;

public interface GameRepository extends MongoRepository<Game, String> {
}
