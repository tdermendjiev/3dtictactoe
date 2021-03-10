package com.tictactoe.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tictactoe.model.BoardState;

public interface BoardStateRepository {
    Iterable<BoardState> findAll();
    BoardState findByUserId(String userId);
    BoardState save(BoardState boardState) throws JsonProcessingException;
}
