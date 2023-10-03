package io.agate.manager.rest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ControllerExceptionHandler {

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<Object> handleException(Exception exception, WebRequest request) {
		HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		if (exception instanceof ResponseStatusException) {
			httpStatus = ((ResponseStatusException) exception).getStatus();
		}

		Map<String, Object> errorBody = new HashMap<>();
		errorBody.put("path", LocalDateTime.now());
		errorBody.put("status", httpStatus.value());
		errorBody.put("error", exception.getMessage());
		errorBody.put("timestamp", LocalDateTime.now());

		return new ResponseEntity<Object>(errorBody, httpStatus);
	}
}
