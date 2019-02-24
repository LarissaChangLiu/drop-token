package com.larissa.liu.droptoken.resources;

import com.larissa.liu.droptoken.api.DropTokenService;
import com.larissa.liu.droptoken.errorhandling.DropTokenException;
import com.larissa.liu.droptoken.model.request.CreateGameRequest;
import com.larissa.liu.droptoken.model.request.PostMoveRequest;
import com.larissa.liu.droptoken.model.response.GetMovesResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 */
@Path("/drop_token")
@Produces(MediaType.APPLICATION_JSON)
public class DropTokenResource {
    private static final Logger logger = LoggerFactory.getLogger(DropTokenResource.class);

    private DropTokenService dropTokenService;
    
    public DropTokenResource() {
    	this.dropTokenService = new DropTokenService(); 
    }

//	GET /drop_token - Return all in-progress games.
//	Output
//	{ "games" : ["gameid1", "gameid2"] }
//	Status codes
//	• 200 - OK. On success
    
    @GET
    public Response getGames() throws DropTokenException {
        return Response.ok(dropTokenService.getGames()).build();
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
    
    @POST
    public Response createNewGame(CreateGameRequest request) throws DropTokenException {
        logger.info("request={}", request);
        return Response.ok(dropTokenService.createGame(request)).build();
    }
    
//    GET /drop_token/{gameId} - Get the state of the game.
//    Output:
//    { "players" : ["player1", "player2"], # Initial list of players.
//    "state": "DONE/IN_PROGRESS",
//    "winner": "player1", # in case of draw, winner will be null, state will be DONE.
//    # in case game is still in progress, key should not exist.
//    }
//    
//    Status codes
//    • 200 - OK. On success
//    • 400 - Malformed request
//    • 404 - Game/moves not found.

    @Path("/{id}")
    @GET
    public Response getGameStatus(@PathParam("id") String gameId) throws DropTokenException {
        logger.info("gameId = {}", gameId);
        return Response.ok(dropTokenService.getGameStatus(gameId)).build();
    }
    
//    POST /drop_token/{gameId}/{playerId} - Post a move.
//    Input:
//    {
//    "column" : 2
//    }
//    Output:
//    {
//    "move": "{gameId}/moves/{move_number}"
//    }
//    Status codes
//    • 200 - OK. On success
//    • 400 - Malformed input. Illegal move
//    • 404 - Game not found or player is not a part of it.
//    • 409 - Player tried to post when it’s not their turn.

    @Path("/{id}/{playerId}")
    @POST
    public Response postMove(@PathParam("id")String gameId, @PathParam("playerId") String playerId, PostMoveRequest request) throws DropTokenException {
        logger.info("gameId={}, playerId={}, move={}", gameId, playerId, request);
        return Response.ok(dropTokenService.postMove(gameId, playerId, request)).build();
    }

//    DELETE /drop_token/{gameId}/{playerId} - Player quits from
//    game.
//    Status codes
//    • 202 - OK. On success
//    • 404 - Game not found or player is not a part of it.
//    • 410 - Game is already in DONE state.
    
    @Path("/{id}/{playerId}")
    @DELETE
    public Response playerQuit(@PathParam("id")String gameId, @PathParam("playerId") String playerId) throws DropTokenException {
        logger.info("gameId={}, playerId={}", gameId, playerId);
        dropTokenService.deletePlayer(gameId, playerId);
        return Response.status(202).build();
    }
    
//    GET /drop_token/{gameId}/moves- Get (sub) list of the moves
//    played.
//    Optional Query parameters: GET /drop_token/{gameId}/moves?start=0&until=1.
//    Output:
//    {
//    "moves": [
//    {"type": "MOVE", "player": "player1", "column":1},
//    {"type": "QUIT", "player": "player2"}
//    ]
//    }
//    Status codes
//    • 200 - OK. On success
//    • 400 - Malformed request
//    • 404 - Game/moves not found.
    
    @Path("/{id}/moves")
    @GET
    public Response getMoves(@PathParam("id") String gameId, @QueryParam("start") Integer start, @QueryParam("until") Integer until) throws DropTokenException 
    {
        logger.info("gameId={}, start={}, until={}", gameId, start, until);
        GetMovesResponse getMoveResponse = null;
        if (start != null && until == null){
        	getMoveResponse = dropTokenService.getMovesStart(gameId, start);
        } else if (start == null && until != null)
        {
        	getMoveResponse = dropTokenService.getMovesUntil(gameId, until);
        } else if (start != null && until != null)
        {
        	getMoveResponse = dropTokenService.getMovesStartUntil(gameId, start, until);
        } else {
        	getMoveResponse = dropTokenService.getMoves(gameId);
        }
        return Response.ok(getMoveResponse).build();
    }
    
//    GET /drop_token/{gameId}/moves/{move_number} - Return the
//    move.
//    Output:
//    {
//    "type" : "MOVE",
//    "player": "player1",
//    "column": 2
//    }
//    Status codes
//    • 200 - OK. On success
//    • 400 - Malformed request
//    • 404 - Game/moves not found.
    

    @Path("/{id}/moves/{moveId}")
    @GET
    public Response getMove(@PathParam("id") String gameId, @PathParam("moveId") Integer moveId) throws DropTokenException {
        logger.info("gameId={}, moveId={}", gameId, moveId);
        return Response.ok(dropTokenService.getMove(gameId, moveId)).build();
    }

}
