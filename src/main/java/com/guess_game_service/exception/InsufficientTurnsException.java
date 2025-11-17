package com.guess_game_service.exception;

public class InsufficientTurnsException extends RuntimeException {
    public InsufficientTurnsException(String message) {
        super(message);
    }
}
