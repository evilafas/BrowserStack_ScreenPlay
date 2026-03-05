package com.browserstack.exceptions;

public class ApplicationOpenException extends RuntimeException {

    public ApplicationOpenException(String mensaje) {
        super(mensaje);
    }

    public ApplicationOpenException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
