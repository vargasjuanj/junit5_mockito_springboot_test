package com.example.springboot_test.models;

import com.example.springboot_test.exceptions.DineroSuficienteExcepcion;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name="cuentas")
public class Cuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;
    private String persona;
    private BigDecimal saldo;

    public Cuenta(){

    }

    public Cuenta(Long id, String persona, BigDecimal saldo) {
        this.id = id;
        this.persona = persona;
        this.saldo = saldo;
    }

    public void debito (BigDecimal monto) {
        BigDecimal nuevoSaldo = this.saldo.subtract(monto);
        // validamos para que no haya saldo negativo
        if(nuevoSaldo.compareTo(BigDecimal.ZERO) < 0){
            throw new DineroSuficienteExcepcion("Dinero Insuficiente");
        }
        // bigdecimal es inmutable por eso hay que volver a asignar a saldo el resultado de cierto oepración sobre el

        this.saldo = nuevoSaldo;
    }

    public void credito(BigDecimal monto){
        this.saldo = this.saldo.add(monto);


    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cuenta cuenta = (Cuenta) o;
        return id == cuenta.id && Objects.equals(persona, cuenta.persona) && Objects.equals(saldo, cuenta.saldo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, persona, saldo);
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }



}
