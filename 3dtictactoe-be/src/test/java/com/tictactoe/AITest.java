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
public class AITest {

    String userId = "some user id";

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
    public void firstMoveAllowed() {
        BoardState board = threeBoardUserSecond();
        AI ai = new AI(board);
        assertEquals(ai.makeMove(), true);
    }

    @Test
    public void firstMoveNotAllowed() {
        BoardState board = threeBoardUserFirst();
        AI ai = new AI(board);
        assertEquals(ai.makeMove(), false);
    }

    @Test
    public void defensivePlay() {
        BoardState board = threeBoardUserFirst();
        AI ai = new AI(board);
        Move p1 = new Move(0, 0, 0);
        board.addMove(p1, 1);

        ai.makeMove();

        Move p2 = new Move(0, 1, 1);
        board.addMove(p2, 1);

        ai.makeMove();

        assertEquals(board.checkState(0, 2, 2, 2), true);
    }

    @Test
    public void offensivePlay() {
        BoardState board = threeBoardUserSecond();
        AI ai = new AI(board);

        ai.makeMove();

        Move p1 = new Move(0, 0, 0);
        board.addMove(p1, 1);

        ai.makeMove();

        Move p2 = new Move(1, 1, 2);
        board.addMove(p2, 1);

        ai.makeMove();

        assertEquals(ai.win, true);
    }
}