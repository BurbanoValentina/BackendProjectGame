package com.example.gamebackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.gamebackend.model.MultiplayerRoom;

public interface MultiplayerRoomRepository extends MongoRepository<MultiplayerRoom, String> {
}
