package com.tictactoe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tictactoe.model.BoardOptions;
import com.tictactoe.model.BoardState;
import com.tictactoe.model.Move;
import com.tictactoe.repository.JdbcBoardStateRepository;
import com.tictactoe.service.AI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class BoardTest {

    String userId = "some user id";

    @InjectMocks
    AI ai;

    @Mock
    BoardState board;

    private BoardState threeBoardUserSecond() {
        BoardOptions opts = new BoardOptions(3, 0);
        BoardState board = new BoardState(userId, new int[opts.length][opts.length][opts.length], opts.isPlayerTurn, opts.length);
        return board;
    }

    private BoardState threeBoardUserFirst() {
        BoardOptions opts = new BoardOptions(3, 1);
        BoardState board = new BoardState(userId, new int[opts.length][opts.length][opts.length], opts.isPlayerTurn, opts.length);
        return board;
    }

    @Test
    public void createBoard() throws JsonProcessingException {
        BoardState board = threeBoardUserSecond();

        assertEquals(board.getLength(), 3);
    }

    @Test
    public void firstPlayer() {
        BoardState board = threeBoardUserSecond();

        Move move = new Move(0, 0, 0);

        assertEquals(board.addMove(move, 1), false);
        assertEquals(board.addMove(move, 2), true);
    }

    @Test
    public void playerOrder() {
        BoardState board = threeBoardUserSecond();

        Move moveCPU = new Move(0, 0, 0);
        assertEquals(board.addMove(moveCPU, 2), true);
        Move moveHuman = new Move(0, 1, 0);
        assertEquals(board.addMove(moveHuman, 1), true);

    }

    @Test
    public void wrongMove() {
        BoardState board = threeBoardUserSecond();

        Move moveCPU = new Move(0, 0, 0);
        assertEquals(board.addMove(moveCPU, 2), true);

        assertEquals(board.addMove(moveCPU, 1), false);
    }



}