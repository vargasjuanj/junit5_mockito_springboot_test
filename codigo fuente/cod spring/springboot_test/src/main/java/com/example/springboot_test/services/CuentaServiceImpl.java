package com.example.springboot_test.services;

import com.example.springboot_test.models.Banco;
import com.example.springboot_test.models.Cuenta;
import com.example.springboot_test.repositories.BancoRepository;
import com.example.springboot_test.repositories.CuentaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CuentaServiceImpl implements CuentaService{

    //debería llevar el try catch antes de usar el metodo .get() de opcional pero para hacer las pruebas no lo quería alterar
    private CuentaRepository cuentaRepository;
    private BancoRepository bancoRepository;

    public CuentaServiceImpl(CuentaRepository cuentaRepository, BancoRepository bancoRepository) {
        this.cuentaRepository = cuentaRepository;
        this.bancoRepository = bancoRepository;
    }

    @Override
    public Cuenta findById(Long id){
            return cuentaRepository.findById(id).get();
    }

    @Override
    public int revisarTotalTransferencias(Long bancoId) {
        Banco banco =  bancoRepository.findById(bancoId).get();
        return banco.getTotalTransferencias();
    }

    @Override
    public BigDecimal revisarSaldo(Long cuentaId)  {
            Cuenta cuenta =  cuentaRepository.findById(cuentaId).get();
            return cuenta.getSaldo();
    }

    @Override
    public void transferir(Long numCuentaOrigen, Long numCuentaDestino, BigDecimal monto, Long bancoId){

            Cuenta cuentaOrigen = cuentaRepository.findById(numCuentaOrigen).get();
            cuentaOrigen.debito(monto);
            cuentaRepository.save(cuentaOrigen);

            Cuenta cuentaDestino = cuentaRepository.findById(numCuentaDestino).get();
            cuentaDestino.credito(monto);
            cuentaRepository.save(cuentaDestino);

            // si todo sale bien registramos la transferencia

            // podríasmos obtener el banco de la sesion de usuario o de algun contecto, pero para este caso sería hilar muy fino
            Banco banco = bancoRepository.findById(bancoId).get();
            int totalTransferencias = banco.getTotalTransferencias();
            banco.setTotalTransferencias(++totalTransferencias);
            bancoRepository.save(banco);



    }


}
