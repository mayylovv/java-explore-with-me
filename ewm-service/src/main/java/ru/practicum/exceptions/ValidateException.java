package ru.practicum.exceptions;

public class ValidateException extends RuntimeException {
    public ValidateException(String message) {
        super(message);
    }
}
