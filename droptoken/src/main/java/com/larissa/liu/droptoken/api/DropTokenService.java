package com.larissa.liu.droptoken.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import com.larissa.liu.droptoken.errorhandling.DropTokenException;
import com.larissa.liu.droptoken.model.Game;
import com.larissa.liu.droptoken.model.GameMoveEnum;
import com.larissa.liu.droptoken.model.GameRoom;
import com.larissa.liu.droptoken.model.GameStatusEnum;
import com.larissa.liu.droptoken.model.Move;
import com.larissa.liu.droptoken.model.request.CreateGameRequest;
import com.larissa.liu.droptoken.model.request.PostMoveRequest;
import com.larissa.liu.droptoken.model.response.CreateGameResponse;
import com.larissa.liu.droptoken.model.response.GameStatusResponse;
import com.larissa.liu.droptoken.model.response.GetGamesResponse;
import com.larissa.liu.droptoken.model.response.GetMoveResponse;
import com.larissa.liu.droptoken.model.response.GetMovesResponse;
import com.larissa.liu.droptoken.model.response.PostMoveResponse;

public class DropTokenService {
	
	private GameRoom gameRoom;
	
	public DropTokenService()
	{
		this.gameRoom = new GameRoom();
	}
	
//	GET /drop_token - Return all in-progress games.
//	Output
//	{ "games" : ["gameid1", "gameid2"] }
//	Status codes
//	• 200 - OK. On success
	
	public GetGamesResponse getGames() throws DropTokenException
	{
		List<String> gameIds = this.gameRoom.getGameRoom().entrySet().stream().filter(entry -> 
		entry.getValue().getGameStatus().equals(GameStatusEnum.IN_PROGRESS)).map(entry -> entry.getKey()).collect(Collectors.toList());
		return new GetGamesResponse.Builder().games(gameIds).build();		
	}
	
//	POST /drop_token - Create a new game.
//	Input:
//	{ "players": ["player1", "player2"],
//	"columns": 4,
//	"rows": 4
//	}
//	Output:
//	{ "gameId": "some_string_token"}
//	Status codes
//	• 200 - OK. On success
//	• 400 - Malformed request
	
	public CreateGameResponse createGame(CreateGameRequest creatGameRequest) throws DropTokenException
	{
		//TODO: need to be synchronized
		//malformed request, only support two players for one game
		if (creatGameRequest.getPlayers().size() > 2)
		{
			throw new DropTokenException(400, 
					"Drop token game only support two players",
					"Drop token game only support two players");
		}
		//malformed request, only support 4 X 4 grid
		if (creatGameRequest.getColumns() > 4 || creatGameRequest.getRows() > 4)
		{
			throw new DropTokenException(400, 
					"Drop token game only support 4 X 4 grid",
					"Drop token game only support 4 X 4 grid");
		}
		
		Game game = new Game(creatGameRequest.getRows(), creatGameRequest.getColumns(), this.gameRoom.getNumOfGames(), creatGameRequest.getPlayers());
		this.gameRoom.addGameIntoGameRoom(game);
		return new CreateGameResponse.Builder().gameId(game.getGameId()).build();
	}
	
//	GET /drop_token/{gameId} - Get the state of the game.
//	Output:
//	{ "players" : ["player1", "player2"], # Initial list of players.
//	"state": "DONE/IN_PROGRESS",
//	"winner": "player1", # in case of draw, winner will be null, state will be DONE.
//	# in case game is still in progress, key should not exist.
//	}
//	Status codes
//	• 200 - OK. On success
//	• 400 - Malformed request
//	• 404 - Game/moves not found.
	
	public GameStatusResponse getGameStatus(String gameId) throws DropTokenException
	{
		Game game = this.gameRoom.getGame(gameId);
		if (game == null)
		{
			throw new DropTokenException(404, 
					"Game doesn't exist",
					"Please verify that game is properly generated");
		}
		if (game.getGameStatus().equals(GameStatusEnum.DONE) && game.getWinner() != null)
		{
			return new GameStatusResponse.Builder()
					.players(game.getPlayers())
					.state(game.getGameStatus().toString())
					.winner(game.getWinner()).build();
		} 
		else
		{
			return new GameStatusResponse.Builder()
					.players(game.getPlayers())
					.state(game.getGameStatus().toString())
					.build();
		} 
	}
	
//	GET /drop_token/{gameId}/moves- Get (sub) list of the moves
//	played.
//	Optional Query parameters: GET /drop_token/{gameId}/moves?start=0&until=1.
//	Output:
//	{
//	"moves": [
//	{"type": "MOVE", "player": "player1", "column":1},
//	{"type": "QUIT", "player": "player2"}
//	]
//	}
//	Status codes
//	• 200 - OK. On success
//	• 400 - Malformed request
//	• 404 - Game/moves not found.
	
	public GetMovesResponse getMoves(String gameId) throws DropTokenException
	{
		Game game = this.gameRoom.getGame(gameId);
		//Game doesn't exist , return 404
		if (game == null)
		{
			throw new DropTokenException(404, 
					"Game doesn't exist",
					"Please verify that game is properly generated");
		}
		List<GetMoveResponse> getMoveResponse = this.gameRoom.getGame(gameId).getMoves().stream().map(move -> {
			String type = move.getType().toString();
			String player = move.getPlayer();
			if (move.getColumn().isPresent())
			{
				int column = move.getColumn().get();
				return new GetMoveResponse.Builder()
						.type(type)
						.player(player)
						.column(column)
						.build();
			} else {
				return new GetMoveResponse.Builder()
						.type(type)
						.player(player)
						.build();
			}
		}).collect(Collectors.toList());
		return new GetMovesResponse.Builder()
				.moves(getMoveResponse)
				.build();
	}
	
	public GetMovesResponse getMovesStartUntil(String gameId, int start, int until) throws DropTokenException
	{
		
		//Malformed request, start shouldn't larger or equal to until, return 400
		if (start >= until)
		{
			throw new DropTokenException(400, 
					"start can't larger or equal to until",
					"start can't larger or equal to until");
		}
		
		//Game doesn't exist, return 404
		Game game = this.gameRoom.getGame(gameId);
		if (game == null)
		{
			throw new DropTokenException(404, 
					"Game doesn't exist",
					"Please verify that game is properly generated");
		}
		
		if (until > game.getMoves().size())
		{
			until = game.getMoves().size();
		}
		if (start < 0)
		{
			start = 0;
		}
		List<GetMoveResponse> getMoveResponse = game.getMoves().subList(start, until).stream().map(move -> {
			String type = move.getType().toString();
			String player = move.getPlayer();
			if (move.getColumn().isPresent())
			{
				int column = move.getColumn().get();
				return new GetMoveResponse.Builder()
						.type(type)
						.player(player)
						.column(column)
						.build();
			} else {
				return new GetMoveResponse.Builder()
						.type(type)
						.player(player)
						.build();
			}
		}).collect(Collectors.toList());
		return new GetMovesResponse.Builder()
				.moves(getMoveResponse)
				.build();	
	}
	
	public GetMovesResponse getMovesStart(String gameId, int start) throws DropTokenException
	{
		//Game doesn't exist, return 404
		Game game = this.gameRoom.getGame(gameId);
		if (game == null)
		{
			throw new DropTokenException(404, 
					"Game doesn't exist",
					"Please verify that game is properly generated");
		}
		
		//Malformed request, start shouldn't larger or equal to the number of games, return 400
		int size = game.getMoves().size();
		if (start >= size)
		{
			throw new DropTokenException(400, 
					"start is too large",
					"check nums of moves in that game");
		}
		
		//if user input a start which is less than zero, update start with zero, so that the sublist start with the first move
		if (start < 0) 
		{
			start = 0;
		}
		
		//query moves from start 
		List<GetMoveResponse> getMoveResponse = this.gameRoom.getGame(gameId).getMoves().subList(start, size).stream().map(move -> {
			String type = move.getType().toString();
			String player = move.getPlayer();
			if (move.getColumn().isPresent())
			{
				int column = move.getColumn().get();
				return new GetMoveResponse.Builder()
						.type(type)
						.player(player)
						.column(column)
						.build();
			} 
			else
			{
				return new GetMoveResponse.Builder()
						.type(type)
						.player(player)
						.build();
			}
		}).collect(Collectors.toList());
		return new GetMovesResponse.Builder()
				.moves(getMoveResponse)
				.build();	
	}
	
	public GetMovesResponse getMovesUntil(String gameId, int until) throws DropTokenException
	{
		//Game doesn't exist, return 404
		Game game = this.gameRoom.getGame(gameId);
		if (game == null)
		{
			throw new DropTokenException(404, 
					"Game doesn't exist",
					"Please verify that game is properly generated");
		}
		
		//if user enter a until larger than the number of moves , then update until with the size of moves list , so that program won't be not over flow.
		int size = game.getMoves().size();
		if (until > size){
			until = size;
		}
		
		//query moves from first until the entry point
		List<GetMoveResponse> getMoveResponse = game.getMoves().subList(0, until).stream().map(move -> {
			String type = move.getType().toString();
			String player = move.getPlayer();
			if (move.getColumn().isPresent())
			{
				int column = move.getColumn().get();
				return new GetMoveResponse.Builder()
						.type(type)
						.player(player)
						.column(column)
						.build();
			} else {
				return new GetMoveResponse.Builder()
						.type(type)
						.player(player)
						.build();
			}
		}).collect(Collectors.toList());
		return new GetMovesResponse.Builder()
				.moves(getMoveResponse)
				.build();	
	}
	
//	POST /drop_token/{gameId}/{playerId} - Post a move.
//	Input:
//	{
//	"column" : 2
//	}
//	Output:
//	{
//	"move": "{gameId}/moves/{move_number}"
//	}
//	Status codes
//	• 200 - OK. On success
//	• 400 - Malformed input. Illegal move
//	• 404 - Game not found or player is not a part of it.
//	• 409 - Player tried to post when it’s not their turn.
	
	public PostMoveResponse postMove(String gameId, String playerId, PostMoveRequest postMoveRequest) throws DropTokenException
	{
		//Only support 4 X 4 grid
		if (postMoveRequest.getColumn() > 4)
		{
			throw new DropTokenException(400, 
					"Game only support 4 X 4 grid",
					"malformed input of column");
		}
		//Game doesn't exist
		Game game = this.gameRoom.getGame(gameId);
		if (game == null)
		{
			throw new DropTokenException(404, 
					"Game doesn't exist",
					"Please verify that game is properly generated");
		}
		//Game is finished, shouldn't allow new move
		if (game.getGameStatus().equals(GameStatusEnum.DONE))
		{
			throw new DropTokenException(400, 
					"Game is finished",
					"Game is finished");
		}
		//Player doesn't exist in this game
		if (!game.getPlayers().stream().anyMatch(player -> player.toLowerCase().equals(playerId))){
			throw new DropTokenException(404, 
					"player doesn't exist in this game",
					"malformed input of player");
		}
		//if player doesn't match with current player, shouldn't process the move
		if (game.getNextTurnPlayer() != null && !game.getNextTurnPlayer().toLowerCase().equals(playerId)){
			throw new DropTokenException(409, 
					"player is not suppose to take move at this turn",
					"malformed input of player");
		}
		Move move = new Move(GameMoveEnum.MOVE, playerId, postMoveRequest.getColumn());
		int move_number = game.addMove(move);
		//"{gameId}/moves/{move_number}"
	    this.gameRoom.updateGame(gameId, game);
	    StringBuilder sb = new StringBuilder();
	    String moveLink = sb.append(gameId).append("/moves/").append(move_number).toString();
	    return new PostMoveResponse.Builder().moveLink(moveLink).build();
	} 

//	GET /drop_token/{gameId}/moves/{move_number} - Return the
//	move.
//	Output:
//	{
//	"type" : "MOVE",
//	"player": "player1",
//	"column": 2
//	}
//	Status codes
//	• 200 - OK. On success
//	• 400 - Malformed request
//	• 404 - Game/moves not found.
	
	public GetMoveResponse getMove(String gameId, int moveNum) throws DropTokenException
	{
		if (moveNum < 0) 
		{
			throw new DropTokenException(400, 
					"move number can't be less than 0",
					"move number minus one will be index");
		}
		//Game doesn't exist
		Game game = this.gameRoom.getGame(gameId);
		if (game == null)
		{
			throw new DropTokenException(404, 
					"Game doesn't exist",
					"Please verify that game is properly generated");
		}
		if (moveNum > game.getMoves().size())
		{
			throw new DropTokenException(400, 
					"move number is too large",
					"check numbers of moves in the game");
		}
		Move move = game.getMoves().get(moveNum);
		if (move.getColumn().isPresent()){
			return new GetMoveResponse.Builder()
					.type(move.getType().toString())
					.player(move.getPlayer())
					.column(move.getColumn().get())
					.build();
		} 
		else 
		{
			return new GetMoveResponse.Builder()
					.type(move.getType().toString())
					.player(move.getPlayer())
					.build();
		}
	}

//	DELETE /drop_token/{gameId}/{playerId} - Player quits from
//	game.
//	Status codes
//	• 202 - OK. On success
//	• 404 - Game not found or player is not a part of it.
//	• 410 - Game is already in DONE state.
	
	public void deletePlayer(String gameId, String playerId) throws DropTokenException
	{
		Game game = this.gameRoom.getGame(gameId);
		//Game does exit.-- 404
		if (game == null)
		{
			throw new DropTokenException(404, 
					"Game doesn't exist",
					"Please verify that game is properly generated");
		}
		//Game is already in DONE state.-- 410
		if (game.getGameStatus().equals(GameStatusEnum.DONE)){
			throw new DropTokenException(410, 
					"Game is already finished",
					"check game status or check the winner");
		}
		//Game not found or player is not a part of it. -- 404
		
		if (!game.getPlayers().stream().anyMatch(player -> player.toLowerCase().equals(playerId)))
		{
			throw new DropTokenException(404, 
					"player doesn't exsit in this game",
					"check player list");
		}
		Move move = new Move(GameMoveEnum.QUIT, playerId);
		game.addMove(move);
	}
}
