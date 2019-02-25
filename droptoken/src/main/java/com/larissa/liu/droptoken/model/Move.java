package com.larissa.liu.droptoken.model;

import java.util.Optional;

public class Move {
	private GameMoveEnum type;
    private String player;
    private int column;
    
    public Move() {};
    
    public Move(GameMoveEnum type, String player)
    {
    		this.type = type;
    		this.player = player;
    }
    
    public Move(GameMoveEnum type, String player, int column)
    {
    		this.type = type;
    		this.player = player;
    		this.column = column;
    }

    public void setMoveType(GameMoveEnum type)
    {
    	this.type = type;
    }    
    
    public GameMoveEnum getMoveType()
    {
    	return this.type;
    }
    
	public GameMoveEnum getType()
    {
    		return this.type;
    }
    
    public String getPlayer() 
    {
    		return this.player;
    }
    
    public Optional<Integer> getColumn()
    {
    		return Optional.ofNullable(this.column);
    }
    
}
