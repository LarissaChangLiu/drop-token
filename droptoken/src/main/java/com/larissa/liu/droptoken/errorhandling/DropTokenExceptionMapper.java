package com.larissa.liu.droptoken.errorhandling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 *
 */
public class DropTokenExceptionMapper implements ExceptionMapper<RuntimeException>  {
    private static final Logger logger = LoggerFactory.getLogger(DropTokenExceptionMapper.class);
    public Response toResponse(RuntimeException e) {
        logger.error("Unhandled exception.", e);
        return Response.status(500).build();
    }
    
    public Response toResponse(DropTokenException ex) {
		return Response.status(ex.getStatus())
				.entity(new ErrorMessage(ex))
				.type(MediaType.APPLICATION_JSON).
				build();
	}
}
