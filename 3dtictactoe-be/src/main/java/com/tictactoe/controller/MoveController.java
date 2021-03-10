package com.tictactoe.controller;

import javax.annotation.security.RolesAllowed;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.tictactoe.model.BoardOptions;
import com.tictactoe.model.Move;
import com.tictactoe.model.Player;
import com.tictactoe.repository.BoardStateRepository;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tictactoe.model.BoardState;
import com.tictactoe.service.AI;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/board")
public class MoveController {

    private final BoardStateRepository boardStateRepo;

    @Autowired
    public MoveController (BoardStateRepository boardStateRepo) {
        this.boardStateRepo = boardStateRepo;
    }

    @GetMapping("/")
    @RolesAllowed("ttt-user")
    public BoardState getBoard(HttpServletRequest request) {
        String userId = getUserId(request);
        BoardState board = boardStateRepo.findByUserId(userId);
        return board;

    }

    @PostMapping("/move")
    @RolesAllowed("ttt-user")
    public BoardState move(@RequestBody Move move, HttpServletRequest request) {
        String userId = getUserId(request);

        if (userId == null) {
            return null;
        }

        BoardState board = boardStateRepo.findByUserId(userId);
        if (board.moveAllowed(move)) {
            AI ai = new AI(board);
            board.addMove(move, Player.HUMAN);

            ai.win = ai.checkWin(Player.HUMAN, move);
            if (!ai.win) {
                ai.makeMove();
                if (ai.win) {
                    board.setWinner(Player.CPU);
                }
            } else {
                board.setWinner(Player.HUMAN);
            }

            try {
                this.boardStateRepo.save(board);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            board.setGameOver(ai.win);

        }
        return board;
    }

    @PostMapping("/new")
    @RolesAllowed("ttt-user")
    public BoardState newGame(@RequestBody BoardOptions options, HttpServletRequest request) {
        try {
            String userId = getUserId(request);
            int length = options.length;
            BoardState board = new BoardState(userId, new int[length][length][length], options.isPlayerTurn, length);

            if (options.isPlayerTurn == 0) {
                AI ai = new AI(board);
                ai.makeMove();
            }

            this.boardStateRepo.save(board);
            return board;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

    }

    private void handleNoSecurityContext(ServletRequest request) {

    }

    private String getUserId(HttpServletRequest request) {
        if (!(request instanceof HttpServletRequest)) {
            throw new RuntimeException("Not a a HTTP request");
        }

        RefreshableKeycloakSecurityContext context = (RefreshableKeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());

        if (context == null) {
            handleNoSecurityContext(request);
            return null;
        }

        AccessToken accessToken = context.getToken();
        IDToken idToken = context.getIdToken();
        Object claims = accessToken.getOtherClaims();
        Object key = accessToken.getOtherClaims().get("user_id");
        String userId = key.toString();
        return userId;
    }

}
