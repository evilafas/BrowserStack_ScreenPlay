package com.browserstack.exceptions;

public class ElementNotFoundException extends RuntimeException {

    public ElementNotFoundException(String mensaje) {
        super(mensaje);
    }

    public ElementNotFoundException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
