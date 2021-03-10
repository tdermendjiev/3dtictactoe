package com.tictactoe.model;

public class Move {
    public int board;
    public int row;
    public int column;

    public Move(int board, int row, int column) {
        this.board = board;
        this.row = row;
        this.column = column;
    }
}
