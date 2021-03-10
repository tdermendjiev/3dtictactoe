package com.tictactoe.model;

public class BoardOptions {
    public int length;
    public int isPlayerTurn;

    public BoardOptions(int length, int isPlayerTurn) {
        this.length = length;
        this.isPlayerTurn = isPlayerTurn;
    }
}
