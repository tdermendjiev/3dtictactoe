package com.tictactoe.model;

public class BoardState {
    private final int length;
    private int isPlayerTurn;
    private int [][][] grid;
    private String userId;
    private boolean isGameOver = false;
    private int winner = 0;

    public BoardState(String userId,int [][][] grid, int isPlayerTurn, int length) {
        this.length = length;
        this.isPlayerTurn = isPlayerTurn;
        this.grid = grid;
        this.userId = userId;
    }

    public int getLength() {
        return length;
    }

    public boolean addMove(Move move, int player) {
        if (this.isGameOver) {
            return false;
        }

        if (this.grid[move.board][move.row][move.column] != 0) {
            return false;
        }
        if (player == Player.HUMAN && this.isPlayerTurn == 1
                || player == Player.CPU && this.isPlayerTurn == 0) {
            this.grid[move.board][move.row][move.column] = player;
            toggleTurn();
            return true;
        }

        return false;

    }

    public void forceAddMove(Move move, int player) {
        this.grid[move.board][move.row][move.column] = player;
    }

    private void toggleTurn() {
        if (this.isPlayerTurn == 1) {
            this.isPlayerTurn = 0;
        } else {
            this.isPlayerTurn = 1;
        }
    }

    public boolean moveAllowed(Move move) {
        return this.grid[move.board][move.row][move.column] == 0;
    }

    public void freeSlot(int board, int row, int column) {
        this.grid[board][row][column] = 0;
    }

    public boolean checkState(int board, int row, int column, int state) {
        return this.grid[board][row][column] == state;
    }

    public void setWinner(int player) {
        this.winner = player;
    }

    public void setGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }

    public int getIsPlayerTurn() {
        return this.isPlayerTurn;
    }

    public void setIsPlayerTurn(int b) {
        this.isPlayerTurn = b;
    }

    public int[][][] getGrid() {
        return this.grid;
    }

    public String getUserId() {
        return this.userId;
    }

    public int getWinner() {
        return this.winner;
    }

    public boolean getIsGameOver() {
        return this.isGameOver;
    }

}