package com.example.springboot_test.repositories;

import com.example.springboot_test.models.Cuenta;

import java.util.List;

public interface CuentaRepository {
    // todas estas firmas de metodos solo la vamos a simular con mockito para devolver supuestos datos de una bd
    List<Cuenta> findAll();
    Cuenta findById(Long id);

    // sería como el save de jpa que se utiliza para guardar y actualziar
    void update(Cuenta cuenta);
}
