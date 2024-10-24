package com.backend.backenddbp.User.Exceptions;

public class BadCredentialException extends Exception{
    public BadCredentialException(String message) {
        super(message);
    }
}
