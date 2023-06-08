package com.example.springboot_test.services;

import com.example.springboot_test.models.Cuenta;

import java.math.BigDecimal;
import java.util.List;

public interface CuentaService {

    List<Cuenta> findAll();
    Cuenta findById(Long id) ;

    Cuenta save(Cuenta cuenta);
    int revisarTotalTransferencias(Long bancoId);

    BigDecimal revisarSaldo (Long cuentaId);

    void transferir(Long numCuentaOrigen, Long numCuentaDestino, BigDecimal monto,
                    Long montoId) ;




}
