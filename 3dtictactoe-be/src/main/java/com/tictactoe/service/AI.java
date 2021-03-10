package com.tictactoe.service;

import com.tictactoe.model.BoardState;
import com.tictactoe.model.Move;
import java.util.ArrayList;
import java.util.List;
import com.tictactoe.model.Player;

public class AI {

    private BoardState board;
    private int[] finalWin;
    public boolean win = false;
    private int lookAheadCounter = 0;
    private int totalLooksAhead = 2;
    private List<List<Integer>>wins = new ArrayList<>();

    public AI(BoardState board) {
        this.board = board;
        this.finalWin = new int[this.board.getLength()];
        setup();
    }

    private void setup() {
        List<List<Integer>>wins = new ArrayList<>();

        // win rows on same board + straightdowns
        for (int j = 0; j < this.board.getLength(); j++) {
            for (int k = 0; k < this.board.getLength(); k++) {
                List<Integer> row = new ArrayList<>();
                List<Integer> column = new ArrayList<>();
                List<Integer> straightDown = new ArrayList<>();
                int firstInRow = j*(this.board.getLength()*this.board.getLength()) + this.board.getLength()*k;
                int firstInColumn = j*(this.board.getLength()*this.board.getLength()) + k;
                int firstInStraightDown = j*this.board.getLength() + k;
                for (int l = 0; l < this.board.getLength(); l++) {
                    row.add(firstInRow+l);
                    column.add(firstInColumn+this.board.getLength()*l);
                    straightDown.add(firstInStraightDown + this.board.getLength()*this.board.getLength()*l);
                }
                wins.add(row);
                wins.add(column);
                wins.add(straightDown);
            }
        }

        //left diagonals
        for (int j = 0; j < this.board.getLength(); j++) {
            int firstInDiag = j * this.board.getLength()*this.board.getLength();
            List<Integer> diagonal = new ArrayList<>();
            for (int k = 0; k < this.board.getLength(); k++) {
                diagonal.add(firstInDiag + k*(this.board.getLength()+1));
            }
            wins.add(diagonal);
        }

        //right diagonals
        for (int j = 0; j < this.board.getLength(); j++) {
            int firstInDiag = j * this.board.getLength()*this.board.getLength() + this.board.getLength()-1;
            List<Integer> diagonal = new ArrayList<>();
            for (int k = 0; k < this.board.getLength(); k++) {
                diagonal.add(firstInDiag + k*(this.board.getLength()-1));
            }
            wins.add(diagonal);
        }



        //TODO: diagonals through boards

        this.wins = wins;
    }

    public boolean makeMove() {
        int bestScore;
        int hValue = 0;
        Move nextMove;
        int bestScoreBoard = -1;
        int bestScoreRow = -1;
        int bestScoreColumn = -1;

        bestScore = -1000;

        for (int i = 0; i < board.getLength(); i++) {
            for (int j = 0; j < board.getLength(); j++) {
                for (int k = 0; k < board.getLength(); k++) {
                    Move move = new Move(i, j, k);
                    if (board.moveAllowed(move)) {
                        nextMove = move;

                        if (checkWin(Player.CPU, nextMove)) {
                            win = true;
                            return this.board.addMove(nextMove, Player.CPU);

                        } else {
                            hValue = lookAhead(Player.HUMAN, -1000, 1000);
                        }

                        lookAheadCounter = 0;

                        if (hValue >= bestScore) {
                            bestScore = hValue;
                            bestScoreBoard = i;
                            bestScoreRow = j;
                            bestScoreColumn = k;
                            this.board.freeSlot(i, j, k);
                        } else {
                            this.board.freeSlot(i, j, k);
                        }
                    }
                }
            }
        }

        if (!win) {
            Move move = new Move(bestScoreBoard, bestScoreRow, bestScoreColumn);
            return this.board.addMove(move, Player.CPU);
        }

        return false;
    }

    public boolean checkWin(int i, Move pos) {
        BoardState boardCopy = new BoardState(this.board.getUserId(), this.board.getGrid(), this.board.getIsPlayerTurn(), this.board.getLength());
        boardCopy.forceAddMove(pos, i);

        int[] gameBoard = new int[this.board.getLength()*this.board.getLength()*this.board.getLength()];

        int counter = 0;

        for (int j = 0; j < boardCopy.getLength(); j++) {
            for (int k = 0; k < boardCopy.getLength(); k++) {
                for (int l = 0; l < boardCopy.getLength(); l++) {
                    if (boardCopy.checkState(j,k,l, i)) {
                        gameBoard[counter] = 1;
                    } else {
                        gameBoard[counter] = 0;
                    }
                    counter++;
                }
            }
        }

        for (int j = 0; j < wins.size(); j++) {
            counter = 0;
            for (int k = 0; k < boardCopy.getLength(); k++) {
                if(gameBoard[wins.get(j).get(k)] == 1) {
                    counter++;

                    finalWin[k] = wins.get(j).get(k);

                    if (counter == boardCopy.getLength()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private int lookAhead(int m, int a, int b) {
        int alpha = a;
        int beta = b;

        if (lookAheadCounter <= totalLooksAhead) {
            lookAheadCounter++;

            if (m == Player.CPU) {
                int hValue;
                Move nextMove;

                for (int i = 0; i < this.board.getLength(); i++) {
                    for (int j = 0; j < this.board.getLength(); j++) {
                        for (int k = 0; k < this.board.getLength(); k++) {
                            Move move = new Move(i, j, k);
                            if (this.board.moveAllowed(move)) {
                                nextMove = move;

                                if (checkWin(Player.CPU, nextMove)) {
                                    this.board.freeSlot(i,j,k);
                                    return 1000;
                                } else {
                                    hValue = lookAhead(Player.HUMAN, alpha, beta);
                                    if (hValue > alpha) {
                                        alpha = hValue;
                                        this.board.freeSlot(i,j,k);
                                    } else {
                                        this.board.freeSlot(i,j,k);
                                    }
                                }

                                if (alpha >= beta) {
                                    break;
                                }
                            }
                        }
                    }
                }
                return alpha;
            } else {
                int hValue;
                Move nextMove;
                for (int i = 0; i < this.board.getLength(); i++) {
                    for (int j = 0; j < this.board.getLength(); j++) {
                        for (int k = 0; k < this.board.getLength(); k++) {
                            Move move = new Move(i, j, k);
                            if (this.board.moveAllowed(move)) {
                                nextMove = move;

                                if (checkWin(Player.HUMAN, nextMove)) {
                                    this.board.freeSlot(i,j,k);
                                    return -1000;
                                } else {
                                    hValue = lookAhead(Player.CPU, alpha, beta);
                                    if (hValue < beta) {
                                        beta = hValue;
                                        this.board.freeSlot(i,j,k);
                                    } else {
                                        this.board.freeSlot(i,j,k);
                                    }

                                    if (alpha >= beta) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                return beta;
            }

        } else {
            return heuristic();
        }
    }

    private int heuristic() {
        return (checkAvailable(Player.CPU) - checkAvailable(Player.HUMAN));
    }

    private int checkAvailable(int m) {
        int winCounter = 0;

        int[] gameBoard = new int[this.board.getLength()*this.board.getLength()*this.board.getLength()];

        int counter = 0;
        for (int i = 0; i < this.board.getLength(); i++) {
            for (int j = 0; j < this.board.getLength(); j++) {
                for (int k = 0; k < this.board.getLength(); k++) {
                    if (this.board.checkState(i,j,k,m) || this.board.moveAllowed(new Move(i,j,k))) {
                        gameBoard[counter] = 1;
                    } else {
                        gameBoard[counter] = 0;
                    }
                    counter++;
                }
            }
        }

        for (int i = 0; i < this.wins.size(); i++) {
            counter = 0;
            for (int j = 0; j < this.board.getLength(); j++) {
                if (gameBoard[wins.get(i).get(j)] == 1) {
                    counter++;
                    finalWin[j] = wins.get(i).get(j);
                    if (counter == this.board.getLength()) {
                        winCounter++;
                    }
                }
            }
        }
        return winCounter;
    }
}
