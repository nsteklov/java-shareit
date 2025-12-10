package ru.practicum.shareit;


import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.shareit.error.ErrorHandler;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

public class ErrorHandlerTest {

    public void  printMessage() {
        ErrorHandler errorHandler = new ErrorHandler();
        errorHandler.printMessage();
    }

    @Test
    public void handleNotFoundException() {
        NotFoundException notFoundException = new NotFoundException("Исключение");
        ErrorResponse errorResponse = new ErrorResponse(notFoundException.getMessage());
        printMessage();
    }

    @Test
    public void handleDuplicatedDataException() {
        DuplicatedDataException duplicatedDataException = new DuplicatedDataException("Исключение");
        ErrorResponse errorResponse = new ErrorResponse(duplicatedDataException.getMessage());
        printMessage();
    }

    @Test
    public void handleValidationException() {
        ValidationException validationException = new ValidationException("Исключение");
        ErrorResponse errorResponse = new ErrorResponse(validationException.getMessage());
        printMessage();
    }
}
