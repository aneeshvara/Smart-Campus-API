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
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {
    @Override
    public Response toResponse(SensorUnavailableException ex) {
        ErrorMessage error = new ErrorMessage(ex.getMessage(), 403,
                "https://smartcampus.api/docs/errors#sensor-unavailable");
        return Response.status(Response.Status.FORBIDDEN)
                .entity(error).type(MediaType.APPLICATION_JSON).build();
    }
}
