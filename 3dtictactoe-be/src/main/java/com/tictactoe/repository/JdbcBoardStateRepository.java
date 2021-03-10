package com.tictactoe.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tictactoe.model.BoardState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class JdbcBoardStateRepository implements BoardStateRepository {

    private JdbcTemplate jdbc;

    @Autowired
    public void JdbcBoardStateRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Iterable<BoardState> findAll() {
        try {
            return jdbc.query("select user_id, grid, length, is_player_turn from BoardState", this::mapRowToBoardState);
        } catch (Exception e) {
            return null;
        }

    }

    private BoardState mapRowToBoardState(ResultSet rs, int i) throws SQLException {
        try {
            ObjectMapper om = new ObjectMapper();
            int[][][] grid = om.readValue(rs.getString("grid"), int[][][].class);
            return new BoardState(rs.getString("user_id"),
                    grid,
                    rs.getInt("is_player_turn"),
                    rs.getInt("length"));
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public BoardState findByUserId(String userId) {
        try {
            return jdbc.queryForObject(
                    "select user_id, grid, length, is_player_turn from BoardState where user_id=?", this::mapRowToBoardState, userId);
        } catch(Exception e) {
            return null;
        }

    }

    @Override
    public BoardState save(BoardState boardState) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        jdbc.update(
                "insert into BoardState (user_id, grid, length, is_player_turn) values (?, ?, ?, ?) on duplicate key update grid = values(grid), length = values(length), is_player_turn = values(is_player_turn)", boardState.getUserId(),
                om.writeValueAsString(boardState.getGrid()),
                boardState.getLength(),
                boardState.getIsPlayerTurn()
                );
        return boardState;
    }
}
