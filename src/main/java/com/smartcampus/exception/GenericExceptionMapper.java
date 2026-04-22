/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exception;

/**
 *
 * @author anees
 */
import com.smartcampus.model.ErrorMessage;
import javax.ws.rs.core.*; import javax.ws.rs.ext.*;
import java.util.logging.*;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOGGER = Logger.getLogger(GenericExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable ex) {
        LOGGER.log(Level.SEVERE, "Unexpected error: " + ex.getMessage(), ex);
        ErrorMessage error = new ErrorMessage(
                "An unexpected internal server error occurred. Please try again later.",
                500, "https://smartcampus.api/docs/errors#internal-server-error");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error).type(MediaType.APPLICATION_JSON).build();
    }
}
