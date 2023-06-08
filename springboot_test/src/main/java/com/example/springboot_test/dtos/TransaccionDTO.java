package com.example.springboot_test.dtos;

import java.math.BigDecimal;

public class TransaccionDTO {
    // al final es un pojo, un dto muy parecido a las entities, pero que no está mapeado en ninguna tabla
        // es solo una clase para recibir los datos del cliente
        // al final se envia un Json y spring se encarga de forma automatica de convertir el json en esta instancia de TransaccionDTO
    private Long cuentaOrigenId;
    private Long cuentaDestinoId;
    private BigDecimal monto;
    private Long bancoId;

    public Long getCuentaOrigenId() {
        return cuentaOrigenId;
    }

    public void setCuentaOrigenId(Long cuentaOrigenId) {
        this.cuentaOrigenId = cuentaOrigenId;
    }

    public Long getCuentaDestinoId() {
        return cuentaDestinoId;
    }

    public void setCuentaDestinoId(Long cuentaDestinoId) {
        this.cuentaDestinoId = cuentaDestinoId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Long getBancoId() {
        return bancoId;
    }

    public void setBancoId(Long bancoId) {
        this.bancoId = bancoId;
    }
}
