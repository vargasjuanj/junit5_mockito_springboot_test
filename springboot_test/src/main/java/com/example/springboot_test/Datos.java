package com.example.springboot_test;

import com.example.springboot_test.models.Banco;
import com.example.springboot_test.models.Cuenta;

import java.math.BigDecimal;
import java.util.Optional;

public class Datos {
    public static Optional<Cuenta> crearCuenta001(){
        // convierte el objeto cuenta en un optional
        return Optional.of( new Cuenta(1L,"Andrés", new BigDecimal("1000")));
    }
    public static Optional<Cuenta> crearCuenta002(){
        return Optional.of(new Cuenta (2L,"Jhon", new BigDecimal("2000")));
    }

    public static Optional<Banco> crearBanco(){

        return Optional.of(new Banco(1L,"El banco financiero", 0));
    }

    public Optional<Cuenta> cuenta1NoEstatica(){
        return Optional.of(new Cuenta(1l,"Andrés", new BigDecimal("1000")));

    }
    public Optional<Cuenta> cuenta2NoEstatica(){
        return Optional.of(new Cuenta(2l,"Jhon", new BigDecimal("2000")));

    }
}
