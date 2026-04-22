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
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {
    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {
        ErrorMessage error = new ErrorMessage(ex.getMessage(), 422,
                "https://smartcampus.api/docs/errors#invalid-room-reference");
        return Response.status(422).entity(error).type(MediaType.APPLICATION_JSON).build();
    }
}
