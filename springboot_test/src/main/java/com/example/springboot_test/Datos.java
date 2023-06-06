package com.example.springboot_test;

import com.example.springboot_test.models.Banco;
import com.example.springboot_test.models.Cuenta;

import java.math.BigDecimal;

public class Datos {
    public static Cuenta crearCuenta001(){
        return new Cuenta(1L,"Andrés", new BigDecimal("1000"));
    }
    public static Cuenta crearCuenta002(){
        Cuenta cuenta = crearCuenta001();
        return new Cuenta(2L,"Jhon", new BigDecimal("2000"));
    }

    public static Banco crearBanco(){
       return new Banco(1L,"El banco financiero", 0);
    }

    public Cuenta cuenta1NoEstatica(){
        return new Cuenta(1l,"Andrés", new BigDecimal("1000"));

    }
    public Cuenta cuenta2NoEstatica(){
        return new Cuenta(2l,"Jhon", new BigDecimal("2000"));

    }
}
