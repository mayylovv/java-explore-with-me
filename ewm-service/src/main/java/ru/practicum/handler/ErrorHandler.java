package ru.practicum.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidateDateException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static ru.practicum.util.Constants.PATTERN_DATE;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotValidException(final Exception exception) {
        log.error("Код ошибки: {}, {}", HttpStatus.BAD_REQUEST, exception.getMessage());
        return Map.of(
                "status", "BAD_REQUEST",
                "reason", "Некорректный запрос",
                "message", exception.getMessage(),
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern(PATTERN_DATE))
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleViolationDateException(final ValidateDateException exception) {
        log.error("Код ошибки: {}, {}", HttpStatus.BAD_REQUEST, exception.getMessage());
        return Map.of(
                "status", "BAD_REQUEST",
                "reason", "Для запрошенной операции условия не выполнены",
                "message", exception.getMessage(),
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern(PATTERN_DATE))
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConstraintViolationException(final RuntimeException exception) {
        log.error("Код ошибки: {}, {}", HttpStatus.CONFLICT, exception.getMessage());
        return Map.of(
                "status", "CONFLICT",
                "reason", "Нарушение целостности",
                "message", exception.getMessage(),
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern(PATTERN_DATE))
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(final Throwable exception) {
        log.error("Код ошибки: {}, {}", HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException notFoundException) {
        log.error("Код ошибки: {}, {}", HttpStatus.NOT_FOUND, notFoundException.getMessage());
        return Map.of(
                "status", "NOT_FOUND",
                "reason", "Нужный объект не найден",
                "message", notFoundException.getMessage(),
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern(PATTERN_DATE))
        );
    }
}
