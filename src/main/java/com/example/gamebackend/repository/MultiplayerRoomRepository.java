package com.example.gamebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gamebackend.model.MultiplayerRoom;

public interface MultiplayerRoomRepository extends JpaRepository<MultiplayerRoom, String> {
}
