package com.example.springboot_test.models;

import javax.persistence.*;

@Entity
@Table(name="bancos")
public class Banco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;
    private String nombre;

    private int totalTransferencias; // lo dejamos en int, en vez de Integer para que defecto parta en cero

    public Banco(){

    }

    public Banco(long id, String nombre, int totalTransferencias) {
        this.id = id;
        this.nombre = nombre;
        this.totalTransferencias = totalTransferencias;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTotalTransferencias() {
        return totalTransferencias;
    }

    public void setTotalTransferencias(int totalTransferencias) {
        this.totalTransferencias = totalTransferencias;
    }

}
