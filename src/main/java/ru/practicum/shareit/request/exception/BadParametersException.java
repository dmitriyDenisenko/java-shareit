package ru.practicum.shareit.request.exception;

public class BadParametersException extends RuntimeException{
    public BadParametersException(String massage){
        super(massage);
    }
}
