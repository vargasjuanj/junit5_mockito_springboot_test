package com.example.springboot_test.services;

import com.example.springboot_test.models.Cuenta;

import java.math.BigDecimal;

public interface CuentaService {
    Cuenta findById(Long id);
    int revisarTotalTransferencias(Long bancoId);

    BigDecimal revisarSaldo (Long cuentaId);

    void transferir(Long numCuentaOrigen, Long numCuentaDestino, BigDecimal monto,
                    Long montoId);




}
