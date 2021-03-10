export class BoardState {
    length: number;
    isPlayerTurn: boolean;
    grid: number[][][];
    userId: String;
    isGameOver: boolean;
    winner: number;

    constructor(data) {
        this.length = data.length;
        this.isPlayerTurn = data.isPlayerTurn;
        this.grid = data.grid;
        this.userId = data.usreId;
        this.isGameOver = data.isGameOver;
        this.winner = data.winner;
    }
}