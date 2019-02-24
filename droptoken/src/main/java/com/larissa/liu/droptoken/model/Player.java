package com.larissa.liu.droptoken.model;

public class Player {
	private String playerName;
	private int playerId;
	
	public Player(String playerName, int playerId){
		this.playerName = playerName;
		this.playerId = playerId;
	}
	
	public String getPlayerName(){
		return this.playerName;
	}
	
	public void setPlayerName(String playerName){
		this.playerName = playerName;
	}
	
	public int getPlayerId(){
		return this.playerId;
	}
	
	public void setPlayerId(int playerId){
		this.playerId = playerId;
	}
}
