package com.teamsphere.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException  {
    public NotFoundException(Long id){super("Entity with id " + id + " not found.");}
}
