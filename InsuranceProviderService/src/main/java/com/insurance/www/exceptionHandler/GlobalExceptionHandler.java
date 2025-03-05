package com.insurance.www.exceptionHandler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.insurance.www.exceptions.CallBackException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(CallBackException.class)
	public ResponseEntity<Map<String, String>> handleCallBackException(CallBackException callBackException) {
		Map<String, String> res = new HashMap<String, String>();
		res.put("Error", callBackException.getMessage());
		return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
	}
}
