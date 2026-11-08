package com.campus.parking.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<?> badCredentials(){return ResponseEntity.status(401).body(Map.of("message","Invalid email or password."));}
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> validation(MethodArgumentNotValidException ex){
        Map<String,String> fields=new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e->fields.putIfAbsent(e.getField(),e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of("message","Validation failed.","errors",fields));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<?> badRequest(IllegalArgumentException ex){return ResponseEntity.badRequest().body(Map.of("message",ex.getMessage()));}
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<?> conflict(){return ResponseEntity.status(409).body(Map.of("message","A record with the same unique value already exists."));}
    @ExceptionHandler(Exception.class)
    ResponseEntity<?> generic(Exception ex){return ResponseEntity.status(500).body(Map.of("message","Unexpected server error. Check backend logs."));}
}
