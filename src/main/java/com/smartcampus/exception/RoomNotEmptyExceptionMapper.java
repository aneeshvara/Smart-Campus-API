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

@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {
    @Override
    public Response toResponse(RoomNotEmptyException ex) {
        ErrorMessage error = new ErrorMessage(ex.getMessage(), 409,
                "https://smartcampus.api/docs/errors#room-not-empty");
        return Response.status(Response.Status.CONFLICT)
                .entity(error).type(MediaType.APPLICATION_JSON).build();
    }
}
