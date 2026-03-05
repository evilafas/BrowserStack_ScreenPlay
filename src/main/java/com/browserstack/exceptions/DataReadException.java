package com.browserstack.exceptions;

public class DataReadException extends RuntimeException {

    public DataReadException(String mensaje) {
        super(mensaje);
    }

    public DataReadException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
