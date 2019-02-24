package com.larissa.liu.droptoken.model;

import java.util.HashMap;
import java.util.Map;

public class GameRoom 
{
	private int numOfGames;
	private Map<String, Game> gameById;
	
	public GameRoom() 
	{
		this.gameById = new HashMap<>();
	}
	
	public int getNumOfGames(){
		return this.numOfGames;
	}
	
	public Map<String, Game> getGameRoom()
	{
		return this.gameById;
	}
	
	public void setGameRoom(Map<String, Game> gameById)
	{
		this.gameById = gameById;
	}
	
	public void addGameIntoGameRoom(Game game)
	{
		this.gameById.put(game.getGameId(), game);
		++this.numOfGames;
	}
	
	public Game getGame(String gameId){
		return this.gameById.get(gameId);
	}
	
	public void updateGame(String gameId, Game game)
	{
		this.gameById.put(gameId, game);
	}
}
