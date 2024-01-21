package ru.practicum.handler;

public class ValidateException extends RuntimeException {
    public ValidateException(String message) {
        super(message);
    }
}