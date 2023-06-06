package com.example.springboot_test.repositories;

import com.example.springboot_test.models.Banco;

import java.util.List;

public interface BancoRepository {

    List<Banco> findAll();
    Banco findById(Long id);
    // sería como el save de jpa que se utiliza para guardar y actualziar
    void update(Banco banco);
}
