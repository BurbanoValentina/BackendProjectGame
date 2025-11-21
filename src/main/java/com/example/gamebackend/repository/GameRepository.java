package com.example.gamebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gamebackend.model.Game;

public interface GameRepository extends JpaRepository<Game, String> {
}
