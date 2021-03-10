import { Component, OnInit } from '@angular/core';
import { AuthService } from '../core/auth.service';
import { ApiService } from '../core/api.service';
import { Subscription, forkJoin } from 'rxjs';
import { Router } from '@angular/router';
import { BoardState } from '../model/BoardState';
import { BoardOptions } from '../model/BoardOptions';
import { Move } from '../model/Move';

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.scss']
})
export class BoardComponent implements OnInit {
  boards:any = [];
  xIsNext: boolean = true;
  winner: number;
  private subscription: Subscription = new Subscription();
  isGameOver = false;
  boardType: any;
  gameTypes = [
    {
      label: "3x3x3",
      length: 3
    },
    {
      label: "4x4x4",
      length: 4
    },
    {
      label: "5x5x5",
      length: 5
    }
  ]
  boardState: BoardState;
  boardOptions: BoardOptions = new BoardOptions();
  gridColumns = "100px 100px 100px"

  constructor(private authService: AuthService, private apiService: ApiService, private router: Router) {}

  ngOnInit() {
    const sub = this.apiService.getCurrent().subscribe((result) => {
      if(result && result.grid){
        this.boardState = new BoardState(result);
        const squares = this.convertSquares(result.grid);
        this.boards = squares;
        this.updateGridStyle(result.length)
      }

    }, error => {
      this.catchError(error);

    });

    this.subscription.add(sub);
  }

  async logout() {
    await this.authService.logout();
   // await this.authService.refresh();
    this.router.navigate(['/login']);
  }

  updateGridStyle(length: number) {
    this.gridColumns = ""
    for (let i = 0; i < length; i++) {
      this.gridColumns += "100px ";
    }
  }

  newGame() {
    const sub = this.apiService.startNew(this.boardOptions).subscribe((result) => {
      this.boardState = new BoardState(result);
      const squares = this.convertSquares(result.grid);
      this.boards = squares;
      this.isGameOver = false;
      this.updateGridStyle(result.length)
      this.xIsNext = true;
    }, error => {
      console.error(error);
    });

    this.subscription.add(sub);
  }

  get player() {
    return 'X';
  }

  makeMove(arr: number, idx: number) {
    if (this.isGameOver) {
      alert("Please start new game");
      return;
    }

    if (!this.xIsNext) {
      return;
    }

    if (!this.boards[arr][idx]) {
      this.boards[arr].splice(idx, 1, this.player);
      this.xIsNext = !this.xIsNext;
    }
    const data = new Move(arr, idx, this.boardState.length);
    const sub = this.apiService.makeMove(data).subscribe((result) => {
      this.boardState = new BoardState(result);
      const squares = this.convertSquares(result.grid);
      this.boards = squares;
      this.isGameOver = result.isGameOver;
      this.winner = result.winner;
      this.xIsNext = result.isPlayerTurn === 1;
    }, error => {

      this.catchError(error);
    });

    this.subscription.add(sub);
  }

  catchError(error) {
    console.error(error);
    if (error.status === 401) {
      //this.router.navigate(['/login']);
    }
  }

  convertSquares(grid): any{
    if (!this.boardState) {
      return;
    }
    let result = [];
    for (let i = 0; i < this.boardState.length; i++) {
      let arr = grid[i].flat()
      for (let y = 0; y < arr.length; y++) {
        const el = arr[y];
        if (el === 0){
          arr[y] = null;
        } else if (el === 1) {
          arr[y] = "X"
        } else if (el === 2) {
          arr[y] = "O"
        }
      }
      result.push(arr);
    }
    return result;
  }


  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  onLengthChange(value, i){
    if (value === "on") {
      this.boardOptions.length = this.gameTypes[i].length
    }
 }
}
