export class Move {
    board: number;
    row: number;
    column: number;

    constructor(arr: number, idx: number, length: number) {
        this.board = arr;
        this.row = Math.floor(idx/length);
        this.column = idx%length;
    }

}