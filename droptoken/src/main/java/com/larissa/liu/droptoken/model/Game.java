package com.larissa.liu.droptoken.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Response;

import com.larissa.liu.droptoken.errorhandling.DropTokenException;

public class Game 
{	
	private String gameId;
	private String winner;
	private String nextTurnPlayer;
	private int emptyPlace;
	private String[][] board;
	private int[] top;
	private List<String> players;
	private List<Move> moves; 
	private GameStatusEnum gameStatus;
	
	public Game(int numOfRow, int numOfCol, int numOfGame, List<String> players) 
	{
		this.board = new String[numOfRow][numOfCol];
		this.top = new int[numOfRow];
		this.emptyPlace = numOfRow * numOfCol;
		Arrays.fill(this.top, -1);
		this.players = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		for (String player : players){
			this.players.add(player);
			sb.append(player);
			sb.append("-");
		}
		this.gameId = sb.append(numOfGame).toString();
		this.gameStatus = GameStatusEnum.IN_PROGRESS;
		//explain why we use arrayList
		this.moves = new ArrayList<>();
	}
	
	public void setNextTurnPlayer(String nextTurnPlayer)
	{
		this.nextTurnPlayer = nextTurnPlayer;
	}
	
	public String getNextTurnPlayer()
	{
		return this.nextTurnPlayer;
	}
	
	public List<Move> getMoves(){
		return this.moves;
	}
	
	public void setMoves(List<Move> moves)
	{
		this.moves = moves;
	}

	public int addMove(Move move) throws DropTokenException
	{
		int org_emptyPlace = this.emptyPlace;
		String org_nextPlayer = this.nextTurnPlayer;
		String player = move.getPlayer().toLowerCase();
		int colIndex = move.getColumn().get() - 1;
		
		if (top[colIndex] + 1 >= this.board.length){
			throw new DropTokenException(Response.Status.CONFLICT.getStatusCode(), 409, 
					"Please try another column, this column is full",
					"check row length");
		}
		if (colIndex < 0){
			throw new DropTokenException(Response.Status.CONFLICT.getStatusCode(), 409, 
					"Please try another column, this column can't be less or equal to 0",
					"check row length");
		}
		try{
			this.moves.add(move);
			if (move.getColumn().isPresent())
			{
				this.board[++top[colIndex]][colIndex] = player;
				String winner = judge(player, top[colIndex], colIndex);
				this.emptyPlace--;
				if (this.emptyPlace == 0 || (winner != null && !winner.isEmpty())){
					this.gameStatus = GameStatusEnum.DONE;
					this.winner = winner;
				}
			} else {
				//player quit
				this.gameStatus = GameStatusEnum.DONE;
			}
			//set up next player name
			this.nextTurnPlayer = this.players.stream().filter(name -> !name.equals(move.getPlayer())).findFirst().get();
		} catch (Exception ex)
		{
			//if exception was threw, restore the status
			this.emptyPlace = org_emptyPlace;
			this.moves.remove(this.moves.size() - 1);
			this.board[top[colIndex]][colIndex] = "";
			this.nextTurnPlayer = org_nextPlayer;
			throw new DropTokenException(Response.Status.CONFLICT.getStatusCode(), 500, 
					"Internal Error",
					"Internal Error");
		}
	    //return move index
		return this.moves.size() - 1;
	}
	
	public void setWinner(String player)
	{
		this.winner = player;
	}
	
	public String getWinner()
	{
		return this.winner;
	}
	
	public void setGameStatus(GameStatusEnum gameStatus)
	{
		this.gameStatus = gameStatus;
	}
	
	public GameStatusEnum getGameStatus()
	{
		return this.gameStatus;
	}
	
	public String getGameId(){
		return this.gameId;
	}
	
	public List<String> getPlayers()
	{
		return this.players;
	}
	
	public void setPlayers(List<String> players)
	{
		this.players = players;
	}
	
	public String[][] getBoard()
	{
		return this.board;
	}
	
	public void setBoard(String[][] board){
		this.board = board;
	}
	
	private String judge(String player, int row, int col)
	{
		if (search(player, row, col, 1,  0) + search(player, row, col, -1,  0) >= 3|| //Horizontal
			search(player, row, col, 1,  1) + search(player, row, col, -1, -1) >= 3|| //diagonal
			search(player, row, col, 0,  1) + search(player, row, col,  0, -1) >= 3|| //vertical
			search(player, row, col, 1, -1) + search(player, row, col, -1,  1) >= 3)  //anti-diagonal
				return player;
			return null;    
	}
	
	private int search(String player, int row, int col, int rowDir, int colDir) {    //searching on one direction
		int colLen = this.board.length;
		int rowLen = this.board[0].length;
		row += rowDir;	
		col += colDir;
		if (col >= colLen || row >= rowLen || row < 0 || col < 0|| this.board[row][col] == null || !this.board[row][col].equals(player))
			return 0;
		return search(player, row , col , rowDir, colDir)+1;
	}	
}
