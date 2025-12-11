package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.error.ErrorHandler;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

public class ErrorHandlerTest {

    @Test
    public void handleNotFoundException() {
        NotFoundException notFoundException = new NotFoundException("Исключение");
        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleNotFound(notFoundException);
    }

    @Test
    public void handleDuplicatedDataException() {
        DuplicatedDataException duplicatedDataException = new DuplicatedDataException("Исключение");
        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleDuplicatedDataException(duplicatedDataException);
    }

    @Test
    public void handleValidationException() {
        ValidationException validationException = new ValidationException("Исключение");
        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleNotValid(validationException);
    }
}
