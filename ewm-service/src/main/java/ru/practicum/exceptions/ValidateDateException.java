package ru.practicum.exceptions;

public class ValidateDateException extends RuntimeException {

    public ValidateDateException(String message) {
        super(message);
    }
}
