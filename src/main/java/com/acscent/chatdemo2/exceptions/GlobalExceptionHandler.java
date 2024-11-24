package com.acscent.chatdemo2.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.acscent.chatdemo2.dto.ErrorResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CodeAlreadyUsedException.class)
    public ResponseEntity<ErrorResponseDTO> handleCodeAlreadyUsedException(CodeAlreadyUsedException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponseDTO response = new ErrorResponseDTO(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CodeNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCodeNotFoundException(CodeNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponseDTO response = new ErrorResponseDTO(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GptResponseParsingException.class)
    public ResponseEntity<ErrorResponseDTO> handleGptResponseParsingException(GptResponseParsingException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponseDTO response = new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponseDTO response = new ErrorResponseDTO(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ImageEncodingException.class)
    public ResponseEntity<ErrorResponseDTO> handleImageEncodingException(ImageEncodingException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponseDTO response = new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PromptLoadingException.class)
    public ResponseEntity<ErrorResponseDTO> handlePromptLoadingException(PromptLoadingException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponseDTO response = new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidLanguageInputException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidLanguageInputException(InvalidLanguageInputException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponseDTO response = new ErrorResponseDTO(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GptImageProcessingException.class)
    public ResponseEntity<ErrorResponseDTO> handleGptImageProcessingException(GptImageProcessingException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponseDTO response = new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoteNotFoundException(NoteNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponseDTO response = new ErrorResponseDTO(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PerfumeNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoteNotFoundException(PerfumeNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponseDTO response = new ErrorResponseDTO(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleFileNotFoundException(ImageNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponseDTO response = new ErrorResponseDTO(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<ErrorResponseDTO> handleImageUploadException(ImageUploadException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponseDTO response = new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}