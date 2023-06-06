package com.example.springboot_test.exceptions;

public class DineroSuficienteExcepcion extends  RuntimeException{
    public DineroSuficienteExcepcion(String message) {
        super(message);
    }
}
