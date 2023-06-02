package ejemplos.model;

import ejemplos.excepion.DineroinsuficienteException;

import java.math.BigDecimal;

public class Cuenta {
    private String persona;
    private BigDecimal saldo;

    private Banco banco;

    public Cuenta(String persona, BigDecimal saldo) {
        this.saldo = saldo;
        this.persona = persona;

    }

    public void debito (BigDecimal monto){
        //saldo.subtract(monto); // asi no funciona, Bigdecimal es preciso e "inumtable" por eso no funca

        BigDecimal nuevoSaldo = this.saldo.subtract(monto);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0){
            throw new DineroinsuficienteException("Dinero Insuficiente");
        }
        this.saldo = nuevoSaldo; // devuelve otra instancia esto, no es que se cambia

    }

    public void credito(BigDecimal monto){
        this.saldo = this.saldo.add(monto);

    }

    @Override
    public boolean equals(Object obj) {
        // obj = null, esta cubierto por la condición de abajo tmb
        if ( !( obj instanceof Cuenta ) ) {
            return false;
        }
        if (this.persona == null || this.saldo == null){
            return false;
        }

        Cuenta c = (Cuenta) obj;

        return this.persona.equals(c.getPersona()) && this.saldo.equals(c.getSaldo());
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

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }
}
